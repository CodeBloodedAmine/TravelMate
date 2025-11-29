package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Notification
import com.example.travelmate.data.room.NotificationDao
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsByUser(userId: String): Flow<List<Notification>> = notificationDao.getNotificationsByUser(userId)
    
    fun getUnreadNotifications(userId: String): Flow<List<Notification>> = notificationDao.getUnreadNotifications(userId)
    
    fun getUnreadCount(userId: String): Flow<Int> = notificationDao.getUnreadCount(userId)
    
    fun getNotificationById(notificationId: String): Flow<Notification?> = notificationDao.getNotificationById(notificationId)
    
    suspend fun insertNotification(notification: Notification) = notificationDao.insertNotification(notification)
    
    suspend fun updateNotification(notification: Notification) = notificationDao.updateNotification(notification)
    
    suspend fun markAsRead(notificationId: String) = notificationDao.markAsRead(notificationId)
    
    suspend fun markAllAsRead(userId: String) = notificationDao.markAllAsRead(userId)
    
    suspend fun deleteNotification(notification: Notification) = notificationDao.deleteNotification(notification)
}

