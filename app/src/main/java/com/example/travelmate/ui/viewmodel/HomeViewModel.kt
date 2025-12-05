package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.Travel
import com.example.travelmate.data.repository.TravelRepositoryHybrid
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val travelRepository: TravelRepositoryHybrid) : ViewModel() {
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
            try {
                val userId = SessionManager.getCurrentUserId()
                if (userId != null) {
                    travelRepository.getOrganisedTravels(userId).collect { travelsList ->
                        _travels.value = travelsList
                        _isLoading.value = false
                    }
                } else {
                    _travels.value = emptyList()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error
            }
        }
    }
    
    fun refresh() {
        loadTravels()
    }
}

