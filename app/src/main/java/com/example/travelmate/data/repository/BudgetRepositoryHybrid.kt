package com.example.travelmate.data.repository

import com.example.travelmate.data.models.BudgetCategory
import com.example.travelmate.data.models.BudgetItem
import com.example.travelmate.data.models.BudgetSummary
import com.example.travelmate.data.room.BudgetDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class BudgetRepositoryHybrid(
    private val budgetDao: BudgetDao,
    private val firebaseService: FirebaseRealtimeService,
    private val networkMonitor: NetworkMonitor
) {
    fun getBudgetItemsByTravel(travelId: String): Flow<List<BudgetItem>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase for real-time updates
            firebaseService.observeBudgetItems(travelId)
        } else {
            // Use local Room database when offline
            budgetDao.getBudgetItemsByTravel(travelId)
        }
    }
    
    suspend fun insertBudgetItem(budgetItem: BudgetItem) {
        // Save to local first
        budgetDao.insertBudgetItem(budgetItem)
        
        // Then sync to Firebase if online
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveBudgetItem(budgetItem)
        }
    }
    
    suspend fun updateBudgetItem(budgetItem: BudgetItem) {
        budgetDao.updateBudgetItem(budgetItem)
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveBudgetItem(budgetItem)
        }
    }
}

