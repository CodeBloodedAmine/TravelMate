package com.example.travelmate.data.room

import androidx.room.*
import com.example.travelmate.data.models.BudgetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_items WHERE id = :budgetId")
    fun getBudgetItemById(budgetId: String): Flow<BudgetItem?>

    @Query("SELECT * FROM budget_items WHERE travelId = :travelId ORDER BY date DESC")
    fun getBudgetItemsByTravel(travelId: String): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budget_items WHERE travelId = :travelId AND category = :category")
    fun getBudgetItemsByCategory(travelId: String, category: com.example.travelmate.data.models.BudgetCategory): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budget_items WHERE travelId = :travelId AND paidByUserId = :userId")
    fun getBudgetItemsByUser(travelId: String, userId: String): Flow<List<BudgetItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItem(budgetItem: BudgetItem)

    @Update
    suspend fun updateBudgetItem(budgetItem: BudgetItem)

    @Delete
    suspend fun deleteBudgetItem(budgetItem: BudgetItem)

    @Query("DELETE FROM budget_items WHERE travelId = :travelId")
    suspend fun deleteBudgetItemsByTravel(travelId: String)

    @Query("DELETE FROM budget_items")
    suspend fun deleteAllBudgetItems()
}

