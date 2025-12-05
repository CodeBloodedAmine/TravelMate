package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.Travel
import com.example.travelmate.data.repository.TravelRepositoryHybrid
import com.example.travelmate.util.SessionManager
import com.example.travelmate.util.ModelHelpers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

class TravelViewModel(private val travelRepository: TravelRepositoryHybrid) : ViewModel() {
    private val _travels = MutableStateFlow<List<Travel>>(emptyList())
    val travels: StateFlow<List<Travel>> = _travels.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadTravels()
    }
    
    fun loadTravels() {
        viewModelScope.launch {
            _isLoading.value = true
            travelRepository.getAllTravels()
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { travelsList ->
                    _travels.value = travelsList
                    _isLoading.value = false
                }
        }
    }
    
    fun getTravelById(travelId: String): StateFlow<Travel?> {
        val flow = MutableStateFlow<Travel?>(null)
        viewModelScope.launch {
            travelRepository.getTravelById(travelId).collect { travel ->
                flow.value = travel
            }
        }
        return flow.asStateFlow()
    }
    
    fun createTravel(
        title: String,
        destination: String,
        description: String?,
        startDate: Long,
        endDate: Long,
        budget: Double
    ) {
        viewModelScope.launch {
            try {
                val userId = SessionManager.getCurrentUserId() ?: return@launch
                
                val travel = ModelHelpers.createTravel(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    organiserId = userId,
                    participantIds = listOf(userId),
                    budget = budget,
                    spentAmount = 0.0,
                    createdAt = System.currentTimeMillis()
                )
                
                travelRepository.insertTravel(travel)
                travelRepository.notifyAllUsersOfNewTravel(travel)
                loadTravels()
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
    
    fun refresh() {
        loadTravels()
    }
    
    fun participateInTravel(travelId: String) {
        viewModelScope.launch {
            try {
                val userId = SessionManager.getCurrentUserId() ?: return@launch
                travelRepository.participateInTravel(travelId, userId)
                loadTravels()
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
}

