package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Activity
import com.example.travelmate.data.room.ActivityDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class ActivityRepositoryHybrid(
    private val activityDao: ActivityDao,
    private val firebaseService: FirebaseRealtimeService,   
    private val networkMonitor: NetworkMonitor
) {
    fun getAllActivities(): Flow<List<Activity>> {
        return if (networkMonitor.isNetworkAvailable()) {
            firebaseService.observeAllActivities()
        } else {
            activityDao.getAllActivities()
        }
    }
    
    fun getActivitiesByTravel(travelId: String): Flow<List<Activity>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase for real-time updates
            firebaseService.observeActivities(travelId)
        } else {
            // Use local Room database when offline
            activityDao.getActivitiesByTravel(travelId)
        }
    }
    
    fun getActivityById(activityId: String): Flow<Activity?> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase for real-time updates
            firebaseService.observeActivity(activityId)
        } else {
            // Use local Room database when offline
            activityDao.getActivityById(activityId)
        }
    }
    
    suspend fun insertActivity(activity: Activity) {
        // Save to local first
        activityDao.insertActivity(activity)
        
        // Then sync to Firebase if online
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveActivity(activity)
        }
    }
    
    suspend fun updateActivity(activity: Activity) {
        activityDao.updateActivity(activity)
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveActivity(activity)
        }
    }
}

