package com.example.data.repository

import com.example.data.cloud.CloudSyncStatus
import com.example.data.cloud.FirestoreSyncManager
import com.example.data.local.ShopDao
import com.example.data.model.AuditLog
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.ExpenseType
import com.example.data.model.ItemCategory
import com.example.data.model.ItemType
import com.example.data.model.PaymentMode
import com.example.data.model.SaleEntry
import com.example.data.model.SalesmanUser
import com.example.data.model.UserRole
import com.example.data.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopRepository(
    private val shopDao: ShopDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    val syncManager = FirestoreSyncManager(shopDao, scope)
    val syncStatus: StateFlow<CloudSyncStatus> = syncManager.syncStatus
    val lastSyncedAt: StateFlow<Long> = syncManager.lastSyncedAt

    init {
        scope.launch {
            seedInitialDataIfNeeded()
        }
    }

    // --- SALES ---
    fun getAllSales(): Flow<List<SaleEntry>> = shopDao.getAllSales()

    fun getSalesByDateRange(startTime: Long, endTime: Long): Flow<List<SaleEntry>> =
        shopDao.getSalesByDateRange(startTime, endTime)

    fun getSalesBySalesmanAndDateRange(salesmanId: String, startTime: Long, endTime: Long): Flow<List<SaleEntry>> =
        shopDao.getSalesBySalesmanAndDateRange(salesmanId, startTime, endTime)

    suspend fun insertSale(sale: SaleEntry): Long = withContext(Dispatchers.IO) {
        val id = shopDao.insertSale(sale)
        val insertedSale = sale.copy(id = id)
        syncManager.pushSale(insertedSale)
        id
    }

    suspend fun updateSale(sale: SaleEntry, editorName: String) = withContext(Dispatchers.IO) {
        shopDao.updateSale(sale)
        syncManager.pushSale(sale)
        val log = AuditLog(
            action = "EDIT_SALE",
            performedBy = editorName,
            details = "Updated sale #${sale.id}: ${sale.effectiveItemName} - ₹${sale.amount} (${sale.paymentMode})"
        )
        val logId = shopDao.insertAuditLog(log)
        syncManager.pushAuditLog(log.copy(id = logId))
    }

    suspend fun voidSale(id: Long, reason: String, voidedBy: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val sale = shopDao.getSaleById(id)
        shopDao.voidSale(id, reason, voidedBy, now, now)
        val updatedSale = sale?.copy(
            isVoided = true,
            voidReason = reason,
            voidedBy = voidedBy,
            voidedAt = now,
            lastModified = now
        )
        if (updatedSale != null) {
            syncManager.pushSale(updatedSale)
        }
        val log = AuditLog(
            action = "VOID_SALE",
            performedBy = voidedBy,
            details = "Voided sale #$id (${sale?.effectiveItemName ?: ""}, ₹${sale?.amount ?: 0.0}). Reason: $reason"
        )
        val logId = shopDao.insertAuditLog(log)
        syncManager.pushAuditLog(log.copy(id = logId))
    }

    // --- EXPENSES ---
    fun getAllExpenses(): Flow<List<ExpenseEntry>> = shopDao.getAllExpenses()

    fun getExpensesByDateRange(startTime: Long, endTime: Long): Flow<List<ExpenseEntry>> =
        shopDao.getExpensesByDateRange(startTime, endTime)

    fun getExpensesBySalesmanAndDateRange(salesmanId: String, startTime: Long, endTime: Long): Flow<List<ExpenseEntry>> =
        shopDao.getExpensesBySalesmanAndDateRange(salesmanId, startTime, endTime)

    suspend fun insertExpense(expense: ExpenseEntry): Long = withContext(Dispatchers.IO) {
        val id = shopDao.insertExpense(expense)
        val insertedExpense = expense.copy(id = id)
        syncManager.pushExpense(insertedExpense)
        id
    }

    // --- SALESMEN ---
    fun getAllSalesmen(): Flow<List<SalesmanUser>> = shopDao.getAllSalesmen()

    fun getActiveSalesmen(): Flow<List<SalesmanUser>> = shopDao.getActiveSalesmen()

    suspend fun getSalesmanByPhone(phone: String): SalesmanUser? = withContext(Dispatchers.IO) {
        shopDao.getSalesmanByPhone(phone)
    }

    suspend fun getSalesmanById(id: String): SalesmanUser? = withContext(Dispatchers.IO) {
        shopDao.getSalesmanById(id)
    }

    suspend fun saveSalesman(user: SalesmanUser, performedBy: String) = withContext(Dispatchers.IO) {
        val existing = shopDao.getSalesmanById(user.id)
        if (existing == null) {
            shopDao.insertSalesman(user)
            syncManager.pushSalesman(user)
            val log = AuditLog(
                action = "CREATE_SALESMAN",
                performedBy = performedBy,
                details = "Created salesman ${user.name} (${user.phone})"
            )
            val logId = shopDao.insertAuditLog(log)
            syncManager.pushAuditLog(log.copy(id = logId))
        } else {
            shopDao.updateSalesman(user)
            syncManager.pushSalesman(user)
            val log = AuditLog(
                action = "EDIT_SALESMAN",
                performedBy = performedBy,
                details = "Updated salesman ${user.name} (${user.phone})"
            )
            val logId = shopDao.insertAuditLog(log)
            syncManager.pushAuditLog(log.copy(id = logId))
        }
    }

    suspend fun deleteSalesman(id: String, performedBy: String) = withContext(Dispatchers.IO) {
        val user = shopDao.getSalesmanById(id)
        shopDao.deleteSalesman(id)
        if (user != null) {
            val log = AuditLog(
                action = "DELETE_SALESMAN",
                performedBy = performedBy,
                details = "Deleted salesman ${user.name} (${user.phone})"
            )
            val logId = shopDao.insertAuditLog(log)
            syncManager.pushAuditLog(log.copy(id = logId))
        }
    }

    // --- CLOSING TALLIES ---
    fun getClosingTally(dateKey: String): Flow<DailyClosingTally?> = shopDao.getClosingTally(dateKey)

    suspend fun getClosingTallySync(dateKey: String): DailyClosingTally? = withContext(Dispatchers.IO) {
        shopDao.getClosingTallySync(dateKey)
    }

    fun getAllClosingTallies(): Flow<List<DailyClosingTally>> = shopDao.getAllClosingTallies()

    suspend fun closeDayTally(tally: DailyClosingTally) = withContext(Dispatchers.IO) {
        shopDao.insertOrUpdateClosingTally(tally)
        syncManager.pushClosingTally(tally)
        val log = AuditLog(
            action = "CLOSE_DAY",
            performedBy = tally.closedBy,
            details = "Closed Day ${tally.dateKey}. Expected Cash: ₹${tally.expectedCashInHand}, Actual: ₹${tally.actualPhysicalCash}, Diff: ₹${tally.cashDifference}"
        )
        val logId = shopDao.insertAuditLog(log)
        syncManager.pushAuditLog(log.copy(id = logId))
    }

    suspend fun reopenDay(dateKey: String, performedBy: String) = withContext(Dispatchers.IO) {
        shopDao.reopenDay(dateKey)
        val existing = shopDao.getClosingTallySync(dateKey)
        if (existing != null) {
            val reopened = existing.copy(isClosed = false)
            syncManager.pushClosingTally(reopened)
        }
        val log = AuditLog(
            action = "REOPEN_DAY",
            performedBy = performedBy,
            details = "Reopened closed sales register for date $dateKey"
        )
        val logId = shopDao.insertAuditLog(log)
        syncManager.pushAuditLog(log.copy(id = logId))
    }

    // --- AUDIT LOGS ---
    fun getAllAuditLogs(): Flow<List<AuditLog>> = shopDao.getAllAuditLogs()

    // --- SEEDING ---
    private suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingSalesmen = shopDao.getAllSalesmen().firstOrNull()
        if (existingSalesmen.isNullOrEmpty()) {
            val owner = SalesmanUser(
                id = "9847000001",
                name = "Syed Ibrahim (Admin)",
                phone = "9847000001",
                pin = "1980",
                role = UserRole.OWNER.name,
                isActive = true
            )
            val salesmen = listOf(
                owner,
                SalesmanUser(
                    id = "9847000002",
                    name = "Fasalu Rahman",
                    phone = "9847000002",
                    pin = "1234",
                    role = UserRole.SALESMAN.name
                ),
                SalesmanUser(
                    id = "9847000003",
                    name = "Anees K",
                    phone = "9847000003",
                    pin = "1234",
                    role = UserRole.SALESMAN.name
                ),
                SalesmanUser(
                    id = "9847000004",
                    name = "Shabeer Ali",
                    phone = "9847000004",
                    pin = "1234",
                    role = UserRole.SALESMAN.name
                ),
                SalesmanUser(
                    id = "9847000005",
                    name = "Niyas PK",
                    phone = "9847000005",
                    pin = "1234",
                    role = UserRole.SALESMAN.name
                )
            )

            salesmen.forEach { shopDao.insertSalesman(it) }

            shopDao.insertAuditLog(
                AuditLog(
                    action = "SYSTEM_INITIALIZE",
                    performedBy = "System",
                    details = "Initialized MJ Garments Sales Register with staff profiles"
                )
            )
        }
    }
}
