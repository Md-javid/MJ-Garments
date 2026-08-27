package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIInsightResult
import com.example.ai.GeminiInsightsService
import com.example.data.model.AuditLog
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.ExpenseType
import com.example.data.model.ItemCategory
import com.example.data.model.ItemType
import com.example.data.model.PaymentMode
import com.example.data.model.SaleEntry
import com.example.data.model.SalesmanUser
import com.example.data.repository.ShopRepository
import com.example.data.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DateFilterMode(val label: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_7_DAYS("Last 7 Days"),
    THIS_MONTH("This Month"),
    ALL_TIME("All History")
}

data class SalesmanPerformance(
    val salesmanId: String,
    val salesmanName: String,
    val totalSales: Double,
    val transactionCount: Int,
    val cashAmount: Double,
    val upiAmount: Double,
    val totalExpenses: Double
)

data class OwnerStats(
    val totalSales: Double = 0.0,
    val cashSales: Double = 0.0,
    val upiSales: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val expectedCashInHand: Double = 0.0,
    val transactionCount: Int = 0,
    val topCategory: String = "N/A",
    val topCategoryAmount: Double = 0.0,
    val topItemType: String = "N/A",
    val topItemAmount: Double = 0.0,
    val categoryBreakdown: Map<String, Double> = emptyMap(),
    val itemTypeBreakdown: Map<String, Double> = emptyMap(),
    val expenseTypeBreakdown: Map<String, Double> = emptyMap(),
    val salesmanLeaderboard: List<SalesmanPerformance> = emptyList()
)

data class DenominationState(
    val count500: Int = 0,
    val count200: Int = 0,
    val count100: Int = 0,
    val count50: Int = 0,
    val count20: Int = 0,
    val count10: Int = 0,
    val coins: Double = 0.0
) {
    val totalAmount: Double
        get() = (count500 * 500.0) + (count200 * 200.0) + (count100 * 100.0) +
                (count50 * 50.0) + (count20 * 20.0) + (count10 * 10.0) + coins
}

data class OwnerUiState(
    val selectedDateFilter: DateFilterMode = DateFilterMode.TODAY,
    val selectedCustomDateKey: String = DateUtils.getTodayDateKey(),
    val searchQuery: String = "",
    val filterCategory: String? = null,
    val filterItemType: String? = null,
    val filterPaymentMode: String? = null,
    val filterSalesmanId: String? = null,
    val showVoidedOnly: Boolean = false,
    val showVoidModalForSale: SaleEntry? = null,
    val showEditModalForSale: SaleEntry? = null,
    val showAddSalesmanDialog: Boolean = false,
    val selectedSalesmanForEdit: SalesmanUser? = null,
    val showReopenConfirmDialog: Boolean = false,
    val isGeneratingAIInsight: Boolean = false,
    val aiInsight: AIInsightResult? = null,
    val snackbarMessage: String? = null,
    val physicalCashInput: String = "",
    val useDenominationCalculator: Boolean = false,
    val denominations: DenominationState = DenominationState(),
    val closingNotes: String = ""
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OwnerViewModel(
    private val repository: ShopRepository,
    private val aiService: GeminiInsightsService = GeminiInsightsService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState: StateFlow<OwnerUiState> = _uiState.asStateFlow()

    private val _dateRange = MutableStateFlow(DateUtils.getTodayStartAndEndTimestamps())

    val allSalesmen: StateFlow<List<SalesmanUser>> = repository.getAllSalesmen()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSales: StateFlow<List<SaleEntry>> = _dateRange.flatMapLatest { (start, end) ->
        if (_uiState.value.selectedDateFilter == DateFilterMode.ALL_TIME) {
            repository.getAllSales()
        } else {
            repository.getSalesByDateRange(start, end)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredExpenses: StateFlow<List<ExpenseEntry>> = _dateRange.flatMapLatest { (start, end) ->
        if (_uiState.value.selectedDateFilter == DateFilterMode.ALL_TIME) {
            repository.getAllExpenses()
        } else {
            repository.getExpensesByDateRange(start, end)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateClosingTally: StateFlow<DailyClosingTally?> = _uiState.flatMapLatest { state ->
        repository.getClosingTally(state.selectedCustomDateKey)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allClosingTallies: StateFlow<List<DailyClosingTally>> = repository.getAllClosingTallies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<OwnerStats> = combine(filteredSales, filteredExpenses) { sales, expenses ->
        computeStats(sales, expenses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OwnerStats())

    fun setDateFilter(filter: DateFilterMode) {
        _uiState.value = _uiState.value.copy(selectedDateFilter = filter)
        when (filter) {
            DateFilterMode.TODAY -> {
                _dateRange.value = DateUtils.getTodayStartAndEndTimestamps()
                _uiState.value = _uiState.value.copy(selectedCustomDateKey = DateUtils.getTodayDateKey())
            }
            DateFilterMode.YESTERDAY -> {
                _dateRange.value = DateUtils.getTodayStartAndEndTimestamps(calendarOffsetDays = -1)
                _uiState.value = _uiState.value.copy(selectedCustomDateKey = DateUtils.formatDateKey(_dateRange.value.first))
            }
            DateFilterMode.LAST_7_DAYS -> {
                _dateRange.value = DateUtils.getDateRangeForDays(7)
            }
            DateFilterMode.THIS_MONTH -> {
                _dateRange.value = DateUtils.getMonthStartAndEndTimestamps()
            }
            DateFilterMode.ALL_TIME -> {
                _dateRange.value = Pair(0L, System.currentTimeMillis() + 86400000)
            }
        }
    }

    fun setCustomDay(year: Int, month: Int, day: Int) {
        val range = DateUtils.getCustomDayTimestamps(year, month, day)
        _dateRange.value = range
        _uiState.value = _uiState.value.copy(
            selectedCustomDateKey = DateUtils.formatDateKey(range.first)
        )
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setFilterCategory(category: String?) {
        _uiState.value = _uiState.value.copy(filterCategory = category)
    }

    fun setFilterItemType(itemType: String?) {
        _uiState.value = _uiState.value.copy(filterItemType = itemType)
    }

    fun setFilterPaymentMode(mode: String?) {
        _uiState.value = _uiState.value.copy(filterPaymentMode = mode)
    }

    fun setFilterSalesman(salesmanId: String?) {
        _uiState.value = _uiState.value.copy(filterSalesmanId = salesmanId)
    }

    fun toggleShowVoidedOnly(show: Boolean) {
        _uiState.value = _uiState.value.copy(showVoidedOnly = show)
    }

    fun clearAllFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            filterCategory = null,
            filterItemType = null,
            filterPaymentMode = null,
            filterSalesmanId = null,
            showVoidedOnly = false
        )
    }

    fun promptVoidSale(sale: SaleEntry) {
        _uiState.value = _uiState.value.copy(showVoidModalForSale = sale)
    }

    fun closeVoidModal() {
        _uiState.value = _uiState.value.copy(showVoidModalForSale = null)
    }

    fun executeVoidSale(sale: SaleEntry, reason: String, ownerName: String) {
        viewModelScope.launch {
            repository.voidSale(sale.id, reason, ownerName)
            _uiState.value = _uiState.value.copy(
                showVoidModalForSale = null,
                snackbarMessage = "Sale #${sale.id} voided."
            )
        }
    }

    fun promptEditSale(sale: SaleEntry) {
        _uiState.value = _uiState.value.copy(showEditModalForSale = sale)
    }

    fun closeEditModal() {
        _uiState.value = _uiState.value.copy(showEditModalForSale = null)
    }

    fun executeEditSale(
        sale: SaleEntry,
        newCategory: String,
        newItemType: String,
        newCustomItem: String?,
        newAmount: Double,
        newPaymentMode: String,
        newNote: String?,
        ownerName: String
    ) {
        viewModelScope.launch {
            val updated = sale.copy(
                category = newCategory,
                itemType = newItemType,
                customItemName = newCustomItem,
                amount = newAmount,
                paymentMode = newPaymentMode,
                note = newNote,
                lastModified = System.currentTimeMillis()
            )
            repository.updateSale(updated, ownerName)
            _uiState.value = _uiState.value.copy(
                showEditModalForSale = null,
                snackbarMessage = "Sale #${sale.id} updated."
            )
        }
    }

    // --- SALESMAN MANAGEMENT ---
    fun openAddSalesmanDialog() {
        _uiState.value = _uiState.value.copy(showAddSalesmanDialog = true, selectedSalesmanForEdit = null)
    }

    fun openEditSalesmanDialog(salesman: SalesmanUser) {
        _uiState.value = _uiState.value.copy(showAddSalesmanDialog = true, selectedSalesmanForEdit = salesman)
    }

    fun closeSalesmanDialog() {
        _uiState.value = _uiState.value.copy(showAddSalesmanDialog = false, selectedSalesmanForEdit = null)
    }

    fun saveSalesmanAccount(
        id: String,
        name: String,
        phone: String,
        pin: String,
        isActive: Boolean,
        isOnLeave: Boolean,
        ownerName: String
    ) {
        viewModelScope.launch {
            val user = SalesmanUser(
                id = id.ifBlank { phone.trim() },
                name = name.trim(),
                phone = phone.trim(),
                pin = pin.trim(),
                isActive = isActive,
                isOnLeave = isOnLeave
            )
            repository.saveSalesman(user, ownerName)
            _uiState.value = _uiState.value.copy(
                showAddSalesmanDialog = false,
                selectedSalesmanForEdit = null,
                snackbarMessage = "Staff account saved: ${user.name}"
            )
        }
    }

    fun toggleSalesmanLeave(salesman: SalesmanUser, isOnLeave: Boolean, ownerName: String) {
        viewModelScope.launch {
            val updated = salesman.copy(isOnLeave = isOnLeave)
            repository.saveSalesman(updated, ownerName)
            val statusStr = if (isOnLeave) "marked On Leave (Access Disabled)" else "marked Active for store"
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "${salesman.name} $statusStr"
            )
        }
    }

    fun deleteSalesmanAccount(salesmanId: String, ownerName: String) {
        viewModelScope.launch {
            repository.deleteSalesman(salesmanId, ownerName)
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Staff account removed"
            )
        }
    }

    // --- CLOSING TALLY ---
    fun setPhysicalCashInput(input: String) {
        _uiState.value = _uiState.value.copy(physicalCashInput = input)
    }

    fun toggleDenominationCalculator(use: Boolean) {
        _uiState.value = _uiState.value.copy(useDenominationCalculator = use)
        if (use) {
            _uiState.value = _uiState.value.copy(
                physicalCashInput = _uiState.value.denominations.totalAmount.toInt().toString()
            )
        }
    }

    fun updateDenomination(
        d500: Int = _uiState.value.denominations.count500,
        d200: Int = _uiState.value.denominations.count200,
        d100: Int = _uiState.value.denominations.count100,
        d50: Int = _uiState.value.denominations.count50,
        d20: Int = _uiState.value.denominations.count20,
        d10: Int = _uiState.value.denominations.count10,
        coins: Double = _uiState.value.denominations.coins
    ) {
        val newDenom = DenominationState(d500, d200, d100, d50, d20, d10, coins)
        _uiState.value = _uiState.value.copy(
            denominations = newDenom,
            physicalCashInput = newDenom.totalAmount.toInt().toString()
        )
    }

    fun setClosingNotes(notes: String) {
        _uiState.value = _uiState.value.copy(closingNotes = notes)
    }

    fun performClosingTally(ownerName: String) {
        val currentStats = stats.value
        val actualCash = _uiState.value.physicalCashInput.toDoubleOrNull() ?: 0.0
        val expectedCash = currentStats.expectedCashInHand
        val difference = actualCash - expectedCash

        val tally = DailyClosingTally(
            dateKey = _uiState.value.selectedCustomDateKey,
            totalCashSales = currentStats.cashSales,
            totalUpiSales = currentStats.upiSales,
            totalExpenses = currentStats.totalExpenses,
            expectedCashInHand = expectedCash,
            actualPhysicalCash = actualCash,
            cashDifference = difference,
            isClosed = true,
            closedBy = ownerName,
            closedAt = System.currentTimeMillis(),
            notes = _uiState.value.closingNotes
        )

        viewModelScope.launch {
            repository.closeDayTally(tally)
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Day ${_uiState.value.selectedCustomDateKey} closed. Cash Diff: ₹${difference.toInt()}"
            )
        }
    }

    fun reopenDay(ownerName: String) {
        viewModelScope.launch {
            repository.reopenDay(_uiState.value.selectedCustomDateKey, ownerName)
            _uiState.value = _uiState.value.copy(
                showReopenConfirmDialog = false,
                snackbarMessage = "Day ${_uiState.value.selectedCustomDateKey} reopened for edits."
            )
        }
    }

    fun showReopenDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showReopenConfirmDialog = show)
    }

    // --- AI INSIGHTS ---
    fun generateAIInsights() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingAIInsight = true)
            val result = aiService.generateSalesAnalysis(
                dateLabel = _uiState.value.selectedDateFilter.label,
                sales = filteredSales.value,
                expenses = filteredExpenses.value,
                closingTally = selectedDateClosingTally.value
            )
            _uiState.value = _uiState.value.copy(
                isGeneratingAIInsight = false,
                aiInsight = result.getOrNull()
            )
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    private fun computeStats(sales: List<SaleEntry>, expenses: List<ExpenseEntry>): OwnerStats {
        val activeSales = sales.filter { !it.isVoided }
        val activeExpenses = expenses.filter { !it.isVoided }

        val totalSales = activeSales.sumOf { it.amount }
        val cashSales = activeSales.filter { it.paymentMode.equals(PaymentMode.CASH.title, ignoreCase = true) }.sumOf { it.amount }
        val upiSales = activeSales.filter { it.paymentMode.equals(PaymentMode.UPI.title, ignoreCase = true) }.sumOf { it.amount }
        val totalExp = activeExpenses.sumOf { it.amount }
        val expectedCash = cashSales - totalExp

        val catMap = mutableMapOf<String, Double>()
        ItemCategory.entries.forEach { catMap[it.title] = 0.0 }
        activeSales.forEach { sale ->
            catMap[sale.category] = (catMap[sale.category] ?: 0.0) + sale.amount
        }

        val itemMap = mutableMapOf<String, Double>()
        ItemType.entries.forEach { itemMap[it.title] = 0.0 }
        activeSales.forEach { sale ->
            itemMap[sale.itemType] = (itemMap[sale.itemType] ?: 0.0) + sale.amount
        }

        val expMap = mutableMapOf<String, Double>()
        ExpenseType.entries.forEach { expMap[it.title] = 0.0 }
        activeExpenses.forEach { exp ->
            expMap[exp.type] = (expMap[exp.type] ?: 0.0) + exp.amount
        }

        val topCat = catMap.maxByOrNull { it.value }
        val topItem = itemMap.maxByOrNull { it.value }

        val salesmanGroup = activeSales.groupBy { it.salesmanId }
        val salesmanExpensesGroup = activeExpenses.groupBy { it.salesmanId }

        val leaderboard = salesmanGroup.map { (salesmanId, salesList) ->
            val salesmanName = salesList.firstOrNull()?.salesmanName ?: salesmanId
            val sTotal = salesList.sumOf { it.amount }
            val sCash = salesList.filter { it.paymentMode.equals(PaymentMode.CASH.title, ignoreCase = true) }.sumOf { it.amount }
            val sUpi = salesList.filter { it.paymentMode.equals(PaymentMode.UPI.title, ignoreCase = true) }.sumOf { it.amount }
            val sExp = salesmanExpensesGroup[salesmanId]?.sumOf { it.amount } ?: 0.0

            SalesmanPerformance(
                salesmanId = salesmanId,
                salesmanName = salesmanName,
                totalSales = sTotal,
                transactionCount = salesList.size,
                cashAmount = sCash,
                upiAmount = sUpi,
                totalExpenses = sExp
            )
        }.sortedByDescending { it.totalSales }

        return OwnerStats(
            totalSales = totalSales,
            cashSales = cashSales,
            upiSales = upiSales,
            totalExpenses = totalExp,
            expectedCashInHand = expectedCash,
            transactionCount = activeSales.size,
            topCategory = topCat?.key ?: "N/A",
            topCategoryAmount = topCat?.value ?: 0.0,
            topItemType = topItem?.key ?: "N/A",
            topItemAmount = topItem?.value ?: 0.0,
            categoryBreakdown = catMap,
            itemTypeBreakdown = itemMap,
            expenseTypeBreakdown = expMap,
            salesmanLeaderboard = leaderboard
        )
    }

    fun generateCategorySeparatedCsv(): String {
        val sales = filteredSales.value.filter { !it.isVoided }
        val dateLabel = _uiState.value.selectedDateFilter.label
        val sb = StringBuilder()

        sb.append("MJ GARMENTS — DETAILED SALES REGISTER\n")
        sb.append("Period:,$dateLabel\n")
        sb.append("Generated At:,${DateUtils.formatFullDateTime(System.currentTimeMillis())}\n\n")

        // CSV Header with Category Columns
        sb.append("Sl No,Time,Bill ID,Staff,COM (₹),CHN (₹),HM (₹),OT (₹),Total Amount (₹),Payment Mode,Item Details,Notes\n")

        var sumCom = 0.0
        var sumChn = 0.0
        var sumHm = 0.0
        var sumOt = 0.0
        var grandTotal = 0.0

        sales.forEachIndexed { index, sale ->
            val comAmt = if (sale.category.equals("COM", ignoreCase = true)) sale.amount else 0.0
            val chnAmt = if (sale.category.equals("CHN", ignoreCase = true)) sale.amount else 0.0
            val hmAmt = if (sale.category.equals("HM", ignoreCase = true)) sale.amount else 0.0
            val otAmt = if (sale.category.equals("OT", ignoreCase = true)) sale.amount else 0.0

            sumCom += comAmt
            sumChn += chnAmt
            sumHm += sumChn
            sumOt += otAmt
            grandTotal += sale.amount

            val itemDetail = (sale.customItemName?.takeIf { it.isNotBlank() } ?: sale.itemType).replace(",", " ")
            val note = (sale.note ?: "").replace(",", " ")

            sb.append("${index + 1},")
            sb.append("${DateUtils.formatDisplayTime(sale.timestamp)},")
            sb.append("#${sale.id},")
            sb.append("\"${sale.salesmanName}\",")
            sb.append("${if (comAmt > 0) comAmt.toInt() else ""},")
            sb.append("${if (chnAmt > 0) chnAmt.toInt() else ""},")
            sb.append("${if (hmAmt > 0) hmAmt.toInt() else ""},")
            sb.append("${if (otAmt > 0) otAmt.toInt() else ""},")
            sb.append("${sale.amount.toInt()},")
            sb.append("${sale.paymentMode},")
            sb.append("\"$itemDetail\",")
            sb.append("\"$note\"\n")
        }

        // Summary Totals Row
        sb.append("\nTOTALS,,,\"ALL STAFF\",${sumCom.toInt()},${sumChn.toInt()},${sumHm.toInt()},${sumOt.toInt()},${grandTotal.toInt()},,,\n")

        return sb.toString()
    }

    fun generateShareableReport(): String {
        val st = stats.value
        val dateLabel = _uiState.value.selectedDateFilter.label
        val tally = selectedDateClosingTally.value

        val sb = StringBuilder()
        sb.append("==============================\n")
        sb.append("MJ GARMENTS — SALES REPORT\n")
        sb.append("Broadway, Kerala | $dateLabel\n")
        sb.append("==============================\n\n")
        sb.append("Total Sales: ${DateUtils.formatCurrency(st.totalSales)} (${st.transactionCount} txns)\n")
        sb.append("Cash Sales: ${DateUtils.formatCurrency(st.cashSales)}\n")
        sb.append("UPI Sales:  ${DateUtils.formatCurrency(st.upiSales)}\n")
        sb.append("Expenses:   ${DateUtils.formatCurrency(st.totalExpenses)}\n")
        sb.append("Net Expected Cash: ${DateUtils.formatCurrency(st.expectedCashInHand)}\n\n")

        sb.append("--- CATEGORY BREAKDOWN ---\n")
        st.categoryBreakdown.forEach { (cat, amt) ->
            if (amt > 0) sb.append("- $cat: ${DateUtils.formatCurrency(amt)}\n")
        }

        sb.append("\n--- ITEM TYPE BREAKDOWN ---\n")
        st.itemTypeBreakdown.forEach { (item, amt) ->
            if (amt > 0) sb.append("- $item: ${DateUtils.formatCurrency(amt)}\n")
        }

        sb.append("\n--- SALESMAN LEADERBOARD ---\n")
        st.salesmanLeaderboard.forEach { sp ->
            sb.append("- ${sp.salesmanName}: ${DateUtils.formatCurrency(sp.totalSales)} (${sp.transactionCount} txns | Cash: ₹${sp.cashAmount.toInt()}, UPI: ₹${sp.upiAmount.toInt()})\n")
        }

        if (tally != null) {
            sb.append("\n--- CLOSING STATUS ---\n")
            sb.append("Status: ${if (tally.isClosed) "CLOSED" else "OPEN"}\n")
            sb.append("Counted Cash: ${DateUtils.formatCurrency(tally.actualPhysicalCash)}\n")
            sb.append("Difference:   ${DateUtils.formatCurrency(tally.cashDifference)}\n")
            sb.append("Closed By:    ${tally.closedBy}\n")
        }

        sb.append("\nGenerated by MJ Garments Tracker")
        return sb.toString()
    }
}
