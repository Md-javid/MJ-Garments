package com.example.data.cloud

import android.util.Log
import com.example.data.local.ShopDao
import com.example.data.model.AuditLog
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.SaleEntry
import com.example.data.model.SalesmanUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CloudSyncStatus(val label: String, val isOnline: Boolean) {
    SYNCED("Cloud Synced", true),
    SYNCING("Syncing...", true),
    OFFLINE_READY("Offline Ready", false),
    ERROR("Cloud Sync Standby", false)
}

class FirestoreSyncManager(
    private val shopDao: ShopDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "FirestoreSync"

    private val _syncStatus = MutableStateFlow(CloudSyncStatus.SYNCED)
    val syncStatus: StateFlow<CloudSyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncedAt = MutableStateFlow(System.currentTimeMillis())
    val lastSyncedAt: StateFlow<Long> = _lastSyncedAt.asStateFlow()

    private var firestore: FirebaseFirestore? = null
    private var salesListener: ListenerRegistration? = null
    private var expensesListener: ListenerRegistration? = null
    private var talliesListener: ListenerRegistration? = null
    private var salesmenListener: ListenerRegistration? = null

    init {
        try {
            val db = FirebaseFirestore.getInstance()
            // Enable offline persistence with unlimited cache
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            db.firestoreSettings = settings
            firestore = db
            _syncStatus.value = CloudSyncStatus.SYNCED
            startRealtimeListeners()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization fallback: ${e.message}")
            _syncStatus.value = CloudSyncStatus.OFFLINE_READY
        }
    }

    private fun startRealtimeListeners() {
        val db = firestore ?: return

        // 1. Listen for remote sales changes
        try {
            salesListener = db.collection(COLLECTION_SALES)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.w(TAG, "Sales listen error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch {
                            for (doc in snapshots.documents) {
                                val sale = doc.toObject(SaleFirestoreDto::class.java)?.toDomainModel()
                                if (sale != null) {
                                    val existing = shopDao.getSaleById(sale.id)
                                    if (existing == null || existing.lastModified < sale.lastModified) {
                                        shopDao.insertSale(sale)
                                    }
                                }
                            }
                            _lastSyncedAt.value = System.currentTimeMillis()
                            _syncStatus.value = CloudSyncStatus.SYNCED
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach sales listener: ${e.message}")
        }

        // 2. Listen for remote expenses
        try {
            expensesListener = db.collection(COLLECTION_EXPENSES)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch {
                            for (doc in snapshots.documents) {
                                val exp = doc.toObject(ExpenseFirestoreDto::class.java)?.toDomainModel()
                                if (exp != null) {
                                    shopDao.insertExpense(exp)
                                }
                            }
                            _lastSyncedAt.value = System.currentTimeMillis()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach expenses listener: ${e.message}")
        }

        // 3. Listen for closing tallies
        try {
            talliesListener = db.collection(COLLECTION_TALLIES)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch {
                            for (doc in snapshots.documents) {
                                val tally = doc.toObject(TallyFirestoreDto::class.java)?.toDomainModel()
                                if (tally != null) {
                                    shopDao.insertOrUpdateClosingTally(tally)
                                }
                            }
                            _lastSyncedAt.value = System.currentTimeMillis()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach tallies listener: ${e.message}")
        }

        // 4. Listen for salesmen accounts
        try {
            salesmenListener = db.collection(COLLECTION_SALESMEN)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch {
                            for (doc in snapshots.documents) {
                                val user = doc.toObject(SalesmanFirestoreDto::class.java)?.toDomainModel()
                                if (user != null) {
                                    shopDao.insertSalesman(user)
                                }
                            }
                            _lastSyncedAt.value = System.currentTimeMillis()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach salesmen listener: ${e.message}")
        }
    }

    // --- PUSH OPERATIONS TO CLOUD ---

    fun pushSale(sale: SaleEntry) {
        val db = firestore ?: return
        scope.launch {
            try {
                _syncStatus.value = CloudSyncStatus.SYNCING
                val docId = if (sale.id > 0) "sale_${sale.id}" else "sale_${sale.timestamp}_${sale.salesmanId}"
                val dto = SaleFirestoreDto.fromDomain(sale)
                db.collection(COLLECTION_SALES)
                    .document(docId)
                    .set(dto, SetOptions.merge())
                    .addOnSuccessListener {
                        _syncStatus.value = CloudSyncStatus.SYNCED
                        _lastSyncedAt.value = System.currentTimeMillis()
                    }
                    .addOnFailureListener {
                        _syncStatus.value = CloudSyncStatus.OFFLINE_READY
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Error pushing sale to cloud: ${e.message}")
                _syncStatus.value = CloudSyncStatus.OFFLINE_READY
            }
        }
    }

    fun pushExpense(expense: ExpenseEntry) {
        val db = firestore ?: return
        scope.launch {
            try {
                _syncStatus.value = CloudSyncStatus.SYNCING
                val docId = if (expense.id > 0) "exp_${expense.id}" else "exp_${expense.timestamp}_${expense.salesmanId}"
                val dto = ExpenseFirestoreDto.fromDomain(expense)
                db.collection(COLLECTION_EXPENSES)
                    .document(docId)
                    .set(dto, SetOptions.merge())
                    .addOnSuccessListener {
                        _syncStatus.value = CloudSyncStatus.SYNCED
                        _lastSyncedAt.value = System.currentTimeMillis()
                    }
                    .addOnFailureListener {
                        _syncStatus.value = CloudSyncStatus.OFFLINE_READY
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Error pushing expense: ${e.message}")
                _syncStatus.value = CloudSyncStatus.OFFLINE_READY
            }
        }
    }

    fun pushClosingTally(tally: DailyClosingTally) {
        val db = firestore ?: return
        scope.launch {
            try {
                _syncStatus.value = CloudSyncStatus.SYNCING
                val dto = TallyFirestoreDto.fromDomain(tally)
                db.collection(COLLECTION_TALLIES)
                    .document("tally_${tally.dateKey}")
                    .set(dto, SetOptions.merge())
                    .addOnSuccessListener {
                        _syncStatus.value = CloudSyncStatus.SYNCED
                        _lastSyncedAt.value = System.currentTimeMillis()
                    }
                    .addOnFailureListener {
                        _syncStatus.value = CloudSyncStatus.OFFLINE_READY
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Error pushing tally: ${e.message}")
                _syncStatus.value = CloudSyncStatus.OFFLINE_READY
            }
        }
    }

    fun pushSalesman(user: SalesmanUser) {
        val db = firestore ?: return
        scope.launch {
            try {
                val dto = SalesmanFirestoreDto.fromDomain(user)
                db.collection(COLLECTION_SALESMEN)
                    .document("user_${user.id}")
                    .set(dto, SetOptions.merge())
            } catch (e: Exception) {
                Log.w(TAG, "Error pushing salesman: ${e.message}")
            }
        }
    }

    fun pushAuditLog(log: AuditLog) {
        val db = firestore ?: return
        scope.launch {
            try {
                val map = mapOf(
                    "action" to log.action,
                    "performedBy" to log.performedBy,
                    "details" to log.details,
                    "timestamp" to log.timestamp
                )
                db.collection(COLLECTION_AUDIT_LOGS)
                    .document("log_${log.timestamp}_${(1000..9999).random()}")
                    .set(map)
            } catch (e: Exception) {
                Log.w(TAG, "Error pushing audit log: ${e.message}")
            }
        }
    }

    fun removeListeners() {
        salesListener?.remove()
        expensesListener?.remove()
        talliesListener?.remove()
        salesmenListener?.remove()
    }

    companion object {
        private const val COLLECTION_SALES = "mj_sales"
        private const val COLLECTION_EXPENSES = "mj_expenses"
        private const val COLLECTION_TALLIES = "mj_daily_tallies"
        private const val COLLECTION_SALESMEN = "mj_salesmen"
        private const val COLLECTION_AUDIT_LOGS = "mj_audit_logs"
    }
}

// --- DTOs FOR FIRESTORE SERIALIZATION ---

data class SaleFirestoreDto(
    var id: Long = 0,
    var category: String = "",
    var itemType: String = "",
    var customItemName: String? = null,
    var amount: Double = 0.0,
    var paymentMode: String = "",
    var salesmanId: String = "",
    var salesmanName: String = "",
    var timestamp: Long = 0,
    var note: String? = null,
    var isSynced: Boolean = true,
    var isVoided: Boolean = false,
    var voidReason: String? = null,
    var voidedBy: String? = null,
    var voidedAt: Long? = null,
    var lastModified: Long = 0
) {
    fun toDomainModel() = SaleEntry(
        id = id,
        category = category,
        itemType = itemType,
        customItemName = customItemName,
        amount = amount,
        paymentMode = paymentMode,
        salesmanId = salesmanId,
        salesmanName = salesmanName,
        timestamp = timestamp,
        note = note,
        isSynced = isSynced,
        isVoided = isVoided,
        voidReason = voidReason,
        voidedBy = voidedBy,
        voidedAt = voidedAt,
        lastModified = lastModified
    )

    companion object {
        fun fromDomain(entry: SaleEntry) = SaleFirestoreDto(
            id = entry.id,
            category = entry.category,
            itemType = entry.itemType,
            customItemName = entry.customItemName,
            amount = entry.amount,
            paymentMode = entry.paymentMode,
            salesmanId = entry.salesmanId,
            salesmanName = entry.salesmanName,
            timestamp = entry.timestamp,
            note = entry.note,
            isSynced = true,
            isVoided = entry.isVoided,
            voidReason = entry.voidReason,
            voidedBy = entry.voidedBy,
            voidedAt = entry.voidedAt,
            lastModified = entry.lastModified
        )
    }
}

data class ExpenseFirestoreDto(
    var id: Long = 0,
    var type: String = "",
    var customType: String? = null,
    var amount: Double = 0.0,
    var recipientNote: String = "",
    var salesmanId: String = "",
    var salesmanName: String = "",
    var timestamp: Long = 0,
    var isSynced: Boolean = true,
    var isVoided: Boolean = false
) {
    fun toDomainModel() = ExpenseEntry(
        id = id,
        type = type,
        customType = customType,
        amount = amount,
        recipientNote = recipientNote,
        salesmanId = salesmanId,
        salesmanName = salesmanName,
        timestamp = timestamp,
        isSynced = isSynced,
        isVoided = isVoided
    )

    companion object {
        fun fromDomain(entry: ExpenseEntry) = ExpenseFirestoreDto(
            id = entry.id,
            type = entry.type,
            customType = entry.customType,
            amount = entry.amount,
            recipientNote = entry.recipientNote,
            salesmanId = entry.salesmanId,
            salesmanName = entry.salesmanName,
            timestamp = entry.timestamp,
            isSynced = true,
            isVoided = entry.isVoided
        )
    }
}

data class TallyFirestoreDto(
    var dateKey: String = "",
    var totalCashSales: Double = 0.0,
    var totalUpiSales: Double = 0.0,
    var totalExpenses: Double = 0.0,
    var expectedCashInHand: Double = 0.0,
    var actualPhysicalCash: Double = 0.0,
    var cashDifference: Double = 0.0,
    var isClosed: Boolean = true,
    var closedBy: String = "",
    var closedAt: Long = 0,
    var notes: String = ""
) {
    fun toDomainModel() = DailyClosingTally(
        dateKey = dateKey,
        totalCashSales = totalCashSales,
        totalUpiSales = totalUpiSales,
        totalExpenses = totalExpenses,
        expectedCashInHand = expectedCashInHand,
        actualPhysicalCash = actualPhysicalCash,
        cashDifference = cashDifference,
        isClosed = isClosed,
        closedBy = closedBy,
        closedAt = closedAt,
        notes = notes
    )

    companion object {
        fun fromDomain(entry: DailyClosingTally) = TallyFirestoreDto(
            dateKey = entry.dateKey,
            totalCashSales = entry.totalCashSales,
            totalUpiSales = entry.totalUpiSales,
            totalExpenses = entry.totalExpenses,
            expectedCashInHand = entry.expectedCashInHand,
            actualPhysicalCash = entry.actualPhysicalCash,
            cashDifference = entry.cashDifference,
            isClosed = entry.isClosed,
            closedBy = entry.closedBy,
            closedAt = entry.closedAt,
            notes = entry.notes
        )
    }
}

data class SalesmanFirestoreDto(
    var id: String = "",
    var name: String = "",
    var phone: String = "",
    var pin: String = "",
    var role: String = "",
    var isActive: Boolean = true,
    var isOnLeave: Boolean = false,
    var createdAt: Long = 0
) {
    fun toDomainModel() = SalesmanUser(
        id = id,
        name = name,
        phone = phone,
        pin = pin,
        role = role,
        isActive = isActive,
        isOnLeave = isOnLeave,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(entry: SalesmanUser) = SalesmanFirestoreDto(
            id = entry.id,
            name = entry.name,
            phone = entry.phone,
            pin = entry.pin,
            role = entry.role,
            isActive = entry.isActive,
            isOnLeave = entry.isOnLeave,
            createdAt = entry.createdAt
        )
    }
}
