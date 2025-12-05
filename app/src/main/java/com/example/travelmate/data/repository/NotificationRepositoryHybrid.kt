package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Notification
import com.example.travelmate.data.room.NotificationDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class NotificationRepositoryHybrid(
    private val notificationDao: NotificationDao,
    private val firebaseService: FirebaseRealtimeService,
    private val networkMonitor: NetworkMonitor
) {
    fun getNotificationsByUser(userId: String): Flow<List<Notification>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase for real-time notifications
            firebaseService.observeNotifications(userId)
        } else {
            // Use local Room database when offline
            notificationDao.getNotificationsByUser(userId)
        }
    }
    
    suspend fun insertNotification(notification: Notification) {
        // Save to local first
        notificationDao.insertNotification(notification)
        
        // Then sync to Firebase if online (will trigger push notification)
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.createNotification(notification)
        }
    }
    
    suspend fun markAsRead(userId: String, notificationId: String) {
        notificationDao.markAsRead(notificationId)
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.markNotificationAsRead(userId, notificationId)
        }
    }
    
    fun getUnreadCount(userId: String): Flow<Int> {
        return notificationDao.getUnreadCount(userId)
    }
    
    suspend fun markAllAsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
        // Firebase updates handled via listeners
    }
}

