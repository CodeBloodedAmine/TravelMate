package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.Activity
import com.example.travelmate.data.models.ActivityCategory
import com.example.travelmate.data.repository.ActivityRepositoryHybrid
import com.example.travelmate.util.ModelHelpers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ActivityViewModel(private val activityRepository: ActivityRepositoryHybrid) : ViewModel() {
    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadActivitiesByTravel(travelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                activityRepository.getActivitiesByTravel(travelId).collect { activitiesList ->
                    _activities.value = activitiesList
                    _isLoading.value = false
                    _error.value = null
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }
    
    fun getActivityById(activityId: String): StateFlow<Activity?> {
        val flow = MutableStateFlow<Activity?>(null)
        viewModelScope.launch {
            activityRepository.getActivityById(activityId).collect { activity ->
                flow.value = activity
            }
        }
        return flow.asStateFlow()
    }
    
    fun createActivity(
        travelId: String,
        title: String,
        description: String?,
        date: Long,
        time: String?,
        location: String?,
        cost: Double = 0.0,
        category: ActivityCategory = ActivityCategory.OTHER
    ) {
        viewModelScope.launch {
            try {
                val activity = ModelHelpers.createActivity(
                    id = UUID.randomUUID().toString(),
                    travelId = travelId,
                    title = title,
                    description = description,
                    date = date,
                    time = time,
                    location = location,
                    assignedParticipantIds = emptyList(),
                    cost = cost,
                    category = category,
                    createdAt = System.currentTimeMillis()
                )
                
                activityRepository.insertActivity(activity)
                _error.value = null
                loadActivitiesByTravel(travelId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun refresh(travelId: String) {
        loadActivitiesByTravel(travelId)
    }
    
    fun clearError() {
        _error.value = null
    }
}

