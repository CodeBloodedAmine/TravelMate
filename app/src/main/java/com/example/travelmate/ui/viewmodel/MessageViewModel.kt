package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.Message
import com.example.travelmate.data.models.MessageType
import com.example.travelmate.data.repository.MessageRepository
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MessageViewModel(private val messageRepository: MessageRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadMessages(travelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                messageRepository.getMessagesByTravel(travelId).collect { messagesList ->
                    _messages.value = messagesList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }
    
    fun sendMessage(travelId: String, content: String) {
        viewModelScope.launch {
            try {
                val senderId = SessionManager.getCurrentUserId() ?: return@launch
                
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    travelId = travelId,
                    senderId = senderId,
                    receiverId = null,
                    content = content,
                    messageType = MessageType.TEXT,
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                
                messageRepository.insertMessage(message)
                loadMessages(travelId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun refresh(travelId: String) {
        loadMessages(travelId)
    }
}

