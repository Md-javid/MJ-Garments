package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ItemCategory(val code: String, val title: String, val description: String) {
    COM("COM", "Company", "Branded company goods"),
    CHN("CHN", "China", "Imported China items"),
    HM("HM", "Handmade", "Handmade artisanal items"),
    OT("OT", "Others", "General / Miscellaneous")
}

enum class ItemType(val title: String) {
    FOOTWEAR("Footwear"),
    BELT("Belt"),
    PURSE("Purse"),
    TSHIRT("T-Shirt"),
    OTHER("Other")
}

enum class PaymentMode(val title: String) {
    CASH("Cash"),
    UPI("UPI")
}

enum class ExpenseType(val title: String) {
    TEA_SNACKS("Tea / Snacks"),
    RENT("Rent"),
    CREDIT_GIVEN("Credit Given"),
    OTHER("Other")
}

enum class UserRole {
    SALESMAN,
    OWNER
}

@Entity(tableName = "sales")
data class SaleEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // COM, CHN, HM, OT
    val itemType: String, // Footwear, Belt, Purse, T-Shirt, Other
    val customItemName: String? = null,
    val amount: Double,
    val paymentMode: String, // CASH, UPI
    val salesmanId: String,
    val salesmanName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null,
    val isSynced: Boolean = true,
    val isVoided: Boolean = false,
    val voidReason: String? = null,
    val voidedBy: String? = null,
    val voidedAt: Long? = null,
    val lastModified: Long = System.currentTimeMillis()
) {
    val effectiveItemName: String
        get() = if (itemType == ItemType.OTHER.title && !customItemName.isNullOrBlank()) {
            customItemName
        } else {
            itemType
        }
}

@Entity(tableName = "expenses")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // Tea / Snacks, Rent, Credit Given, Other
    val customType: String? = null,
    val amount: Double,
    val recipientNote: String,
    val salesmanId: String,
    val salesmanName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true,
    val isVoided: Boolean = false
) {
    val effectiveType: String
        get() = if (type == ExpenseType.OTHER.title && !customType.isNullOrBlank()) {
            customType
        } else {
            type
        }
}

@Entity(tableName = "salesmen")
data class SalesmanUser(
    @PrimaryKey
    val id: String, // phone number or unique id
    val name: String,
    val phone: String,
    val pin: String, // 4-6 digit PIN
    val role: String = UserRole.SALESMAN.name,
    val isActive: Boolean = true,
    val isOnLeave: Boolean = false, // Toggle off staff access when on leave
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "closing_tallies")
data class DailyClosingTally(
    @PrimaryKey
    val dateKey: String, // YYYY-MM-DD in IST
    val totalCashSales: Double,
    val totalUpiSales: Double,
    val totalExpenses: Double,
    val expectedCashInHand: Double,
    val actualPhysicalCash: Double,
    val cashDifference: Double, // actualPhysicalCash - expectedCashInHand
    val isClosed: Boolean = true,
    val closedBy: String,
    val closedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // VOID_SALE, EDIT_SALE, CLOSE_DAY, REOPEN_DAY, CREATE_SALESMAN, EDIT_SALESMAN
    val performedBy: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
