package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLog
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.SaleEntry
import com.example.data.model.SalesmanUser
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    // --- SALES ---
    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SaleEntry>>

    @Query("SELECT * FROM sales WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getSalesByDateRange(startTime: Long, endTime: Long): Flow<List<SaleEntry>>

    @Query("SELECT * FROM sales WHERE salesmanId = :salesmanId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getSalesBySalesmanAndDateRange(salesmanId: String, startTime: Long, endTime: Long): Flow<List<SaleEntry>>

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): SaleEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntry): Long

    @Update
    suspend fun updateSale(sale: SaleEntry)

    @Query("UPDATE sales SET isVoided = 1, voidReason = :reason, voidedBy = :voidedBy, voidedAt = :voidedAt, lastModified = :now WHERE id = :id")
    suspend fun voidSale(id: Long, reason: String, voidedBy: String, voidedAt: Long, now: Long)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expenses WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getExpensesByDateRange(startTime: Long, endTime: Long): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expenses WHERE salesmanId = :salesmanId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getExpensesBySalesmanAndDateRange(salesmanId: String, startTime: Long, endTime: Long): Flow<List<ExpenseEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntry): Long

    // --- SALESMEN ACCOUNTS ---
    @Query("SELECT * FROM salesmen ORDER BY role DESC, name ASC")
    fun getAllSalesmen(): Flow<List<SalesmanUser>>

    @Query("SELECT * FROM salesmen WHERE isActive = 1 ORDER BY role DESC, name ASC")
    fun getActiveSalesmen(): Flow<List<SalesmanUser>>

    @Query("SELECT * FROM salesmen WHERE id = :id LIMIT 1")
    suspend fun getSalesmanById(id: String): SalesmanUser?

    @Query("SELECT * FROM salesmen WHERE phone = :phone LIMIT 1")
    suspend fun getSalesmanByPhone(phone: String): SalesmanUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesman(user: SalesmanUser)

    @Update
    suspend fun updateSalesman(user: SalesmanUser)

    @Query("DELETE FROM salesmen WHERE id = :id")
    suspend fun deleteSalesman(id: String)

    // --- DAILY CLOSING TALLIES ---
    @Query("SELECT * FROM closing_tallies WHERE dateKey = :dateKey LIMIT 1")
    fun getClosingTally(dateKey: String): Flow<DailyClosingTally?>

    @Query("SELECT * FROM closing_tallies WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getClosingTallySync(dateKey: String): DailyClosingTally?

    @Query("SELECT * FROM closing_tallies ORDER BY dateKey DESC")
    fun getAllClosingTallies(): Flow<List<DailyClosingTally>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateClosingTally(tally: DailyClosingTally)

    @Query("UPDATE closing_tallies SET isClosed = 0 WHERE dateKey = :dateKey")
    suspend fun reopenDay(dateKey: String)

    // --- AUDIT LOGS ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    @Query("DELETE FROM sales")
    suspend fun deleteAllSales()

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Query("DELETE FROM closing_tallies")
    suspend fun deleteAllTallies()
}
