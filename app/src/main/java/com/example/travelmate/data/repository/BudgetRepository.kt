package com.example.travelmate.data.repository

import com.example.travelmate.data.models.BudgetCategory
import com.example.travelmate.data.models.BudgetItem
import com.example.travelmate.data.models.BudgetSummary
import com.example.travelmate.data.room.BudgetDao
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    fun getBudgetItemById(budgetId: String): Flow<BudgetItem?> = budgetDao.getBudgetItemById(budgetId)
    
    fun getBudgetItemsByTravel(travelId: String): Flow<List<BudgetItem>> = budgetDao.getBudgetItemsByTravel(travelId)
    
    fun getBudgetItemsByCategory(travelId: String, category: BudgetCategory): Flow<List<BudgetItem>> = 
        budgetDao.getBudgetItemsByCategory(travelId, category)
    
    fun getBudgetItemsByUser(travelId: String, userId: String): Flow<List<BudgetItem>> = 
        budgetDao.getBudgetItemsByUser(travelId, userId)
    
    suspend fun insertBudgetItem(budgetItem: BudgetItem) = budgetDao.insertBudgetItem(budgetItem)
    
    suspend fun updateBudgetItem(budgetItem: BudgetItem) = budgetDao.updateBudgetItem(budgetItem)
    
    suspend fun deleteBudgetItem(budgetItem: BudgetItem) = budgetDao.deleteBudgetItem(budgetItem)

    suspend fun calculateBudgetSummary(travelId: String, totalBudget: Double): BudgetSummary {
        return BudgetSummary(
            totalBudget = totalBudget,
            totalSpent = 0.0,
            remaining = totalBudget,
            expensesByCategory = emptyMap<com.example.travelmate.data.models.BudgetCategory, Double>()
        )
    }
}

