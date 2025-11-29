package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.Notification
import com.example.travelmate.data.repository.NotificationRepository
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = SessionManager.getCurrentUserId() ?: return@launch
                
                notificationRepository.getNotificationsByUser(userId).collect { notificationsList ->
                    _notifications.value = notificationsList
                    _isLoading.value = false
                }
                
                notificationRepository.getUnreadCount(userId).collect { count ->
                    _unreadCount.value = count
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }
    
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
                loadNotifications()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val userId = SessionManager.getCurrentUserId() ?: return@launch
                notificationRepository.markAllAsRead(userId)
                loadNotifications()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun refresh() {
        loadNotifications()
    }
}

