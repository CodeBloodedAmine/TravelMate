package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.BudgetItem
import com.example.travelmate.data.models.BudgetSummary
import com.example.travelmate.data.repository.BudgetRepositoryHybrid
import com.example.travelmate.data.repository.TravelRepositoryHybrid
import com.example.travelmate.util.ModelHelpers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class BudgetViewModel(
    private val budgetRepository: BudgetRepositoryHybrid,
    private val travelRepository: TravelRepositoryHybrid
) : ViewModel() {
    private val _budgetItems = MutableStateFlow<List<BudgetItem>>(emptyList())
    val budgetItems: StateFlow<List<BudgetItem>> = _budgetItems.asStateFlow()
    
    private val _budgetSummary = MutableStateFlow<BudgetSummary?>(null)
    val budgetSummary: StateFlow<BudgetSummary?> = _budgetSummary.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadBudgetItems(travelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                budgetRepository.getBudgetItemsByTravel(travelId).collect { items ->
                    _budgetItems.value = items
                    calculateSummary(travelId)
                    _isLoading.value = false
                    _error.value = null
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }
    
    private suspend fun calculateSummary(travelId: String) {
        try {
            val travel = travelRepository.getTravelById(travelId).first()
            if (travel != null) {
                val items = _budgetItems.value
                val totalSpent = items.sumOf { it.amount }
                val remaining = travel.budget - totalSpent
                
                val expensesByCategory = items.groupBy { it.category }
                    .mapValues { it.value.sumOf { item -> item.amount } }
                
                _budgetSummary.value = BudgetSummary(
                    totalBudget = travel.budget,
                    totalSpent = totalSpent,
                    remaining = remaining,
                    expensesByCategory = expensesByCategory
                )
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    fun addBudgetItem(
        travelId: String,
        title: String,
        amount: Double,
        category: com.example.travelmate.data.models.BudgetCategory,
        paidByUserId: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            try {
                val budgetItem = ModelHelpers.createBudgetItem(
                    id = UUID.randomUUID().toString(),
                    travelId = travelId,
                    title = title,
                    amount = amount,
                    category = category,
                    paidByUserId = paidByUserId,
                    sharedWithUserIds = emptyList(),
                    date = System.currentTimeMillis(),
                    description = description
                )
                
                budgetRepository.insertBudgetItem(budgetItem)
                _error.value = null
                loadBudgetItems(travelId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun refresh(travelId: String) {
        loadBudgetItems(travelId)
    }
    
    fun clearError() {
        _error.value = null
    }
}

