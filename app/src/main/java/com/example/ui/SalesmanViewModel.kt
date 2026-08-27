package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.ItemCategory
import com.example.data.model.SaleEntry
import com.example.data.model.SalesmanUser
import com.example.data.repository.ShopRepository
import com.example.data.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SalesmanFloorUiState(
    val currentSalesman: SalesmanUser? = null,
    val showAddExpenseDialog: Boolean = false,
    val isDayClosed: Boolean = false,
    val snackbarMessage: String? = null
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SalesmanViewModel(
    private val repository: ShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesmanFloorUiState())
    val uiState: StateFlow<SalesmanFloorUiState> = _uiState.asStateFlow()

    private val _currentSalesmanId = MutableStateFlow<String>("")

    // Load today's closing status (to lock new sales if owner closed register)
    val todayClosingTally: StateFlow<DailyClosingTally?> = repository.getClosingTally(DateUtils.getTodayDateKey())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Sales history: ONLY within the last 1 hour
    val recentOneHourSales: StateFlow<List<SaleEntry>> = _currentSalesmanId.flatMapLatest { id ->
        val (start, end) = DateUtils.getTodayStartAndEndTimestamps()
        if (id.isBlank()) {
            repository.getSalesByDateRange(start, end)
        } else {
            repository.getSalesBySalesmanAndDateRange(id, start, end)
        }
    }.map { sales ->
        sales.filter { DateUtils.isWithin1Hour(it.timestamp) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Shop Expenses: ONLY within the last 1 hour
    val recentOneHourExpenses: StateFlow<List<ExpenseEntry>> = _currentSalesmanId.flatMapLatest { id ->
        val (start, end) = DateUtils.getTodayStartAndEndTimestamps()
        if (id.isBlank()) {
            repository.getExpensesByDateRange(start, end)
        } else {
            repository.getExpensesBySalesmanAndDateRange(id, start, end)
        }
    }.map { expenses ->
        expenses.filter { DateUtils.isWithin1Hour(it.timestamp) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSalesman(user: SalesmanUser) {
        _uiState.value = _uiState.value.copy(currentSalesman = user)
        _currentSalesmanId.value = user.id
    }

    fun openAddExpenseDialog() {
        _uiState.value = _uiState.value.copy(showAddExpenseDialog = true)
    }

    fun closeAddExpenseDialog() {
        _uiState.value = _uiState.value.copy(showAddExpenseDialog = false)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun quickSaveSale(
        category: String,
        customWriteUp: String?,
        amount: Double,
        paymentMode: String = "Cash"
    ) {
        val user = _uiState.value.currentSalesman ?: return
        viewModelScope.launch {
            val sale = SaleEntry(
                category = category,
                itemType = if (customWriteUp.isNullOrBlank()) category else "Other",
                customItemName = customWriteUp?.trim()?.takeIf { it.isNotBlank() },
                amount = amount,
                paymentMode = paymentMode,
                salesmanId = user.id,
                salesmanName = user.name,
                timestamp = System.currentTimeMillis(),
                note = null,
                isSynced = true
            )
            repository.insertSale(sale)
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Sale Saved: ₹${amount.toInt()} ($category • $paymentMode)"
            )
        }
    }

    fun saveShopExpense(
        type: String,
        customType: String?,
        amount: Double,
        vendorOrRecipient: String
    ) {
        val user = _uiState.value.currentSalesman ?: return
        viewModelScope.launch {
            val expense = ExpenseEntry(
                type = type,
                customType = customType?.takeIf { it.isNotBlank() },
                amount = amount,
                recipientNote = vendorOrRecipient,
                salesmanId = user.id,
                salesmanName = user.name,
                timestamp = System.currentTimeMillis(),
                isSynced = true
            )
            repository.insertExpense(expense)
            _uiState.value = _uiState.value.copy(
                showAddExpenseDialog = false,
                snackbarMessage = "Shop expense recorded: ₹${amount.toInt()} (${expense.effectiveType})"
            )
        }
    }

    fun voidRecentSale(sale: SaleEntry) {
        val user = _uiState.value.currentSalesman ?: return
        if (!DateUtils.isWithin15Minutes(sale.timestamp)) {
            _uiState.value = _uiState.value.copy(snackbarMessage = "Entry locked (>15 min). Contact Syed Ibrahim to edit.")
            return
        }
        viewModelScope.launch {
            repository.voidSale(sale.id, "Voided by salesman on floor", user.name)
            _uiState.value = _uiState.value.copy(snackbarMessage = "Sale #${sale.id} voided")
        }
    }
}

