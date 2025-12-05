package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.Message
import com.example.travelmate.data.models.MessageType
import com.example.travelmate.data.repository.MessageRepositoryHybrid
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class MessageUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: String? = null,
    val isSending: Boolean = false
)

class MessageViewModel(private val messageRepository: MessageRepositoryHybrid) : ViewModel() {
    private val _uiState = MutableStateFlow(MessageUiState(currentUserId = SessionManager.getCurrentUserId()))
    val uiState: StateFlow<MessageUiState> = _uiState.asStateFlow()
    
    // For backward compatibility
    val messages: StateFlow<List<Message>> = MutableStateFlow(emptyList())
    val isLoading: StateFlow<Boolean> = MutableStateFlow(false)
    
    private var currentTravelId: String = ""
    
    fun loadMessages(travelId: String) {
        currentTravelId = travelId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                messageRepository.getMessagesByTravelId(travelId).collect { messagesList ->
                    _uiState.value = _uiState.value.copy(
                        messages = messagesList.sortedBy { it.timestamp },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erreur lors du chargement des messages: ${e.message}"
                )
            }
        }
    }
    
    fun sendMessage(travelId: String, content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, error = null)
            try {
                val senderId = SessionManager.getCurrentUserId() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        error = "Erreur: Utilisateur non identifié"
                    )
                    return@launch
                }
                
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    travelId = travelId,
                    senderId = senderId,
                    content = content,
                    messageType = "TEXT",
                    timestamp = System.currentTimeMillis(),
                    read = false
                )
                
                messageRepository.sendMessage(message).collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(isSending = false)
                        // Reload messages to show the new one
                        loadMessages(travelId)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSending = false,
                            error = "Erreur lors de l'envoi du message: ${exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = "Erreur lors de l'envoi du message: ${e.message}"
                )
            }
        }
    }
    
    fun markMessagesAsRead(travelId: String) {
        viewModelScope.launch {
            try {
                // Mark all messages in this travel as read
                _uiState.value.messages.forEach { message ->
                    messageRepository.markAsRead(message.id)
                }
            } catch (e: Exception) {
                // Silently fail for read status
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun refresh(travelId: String) {
        loadMessages(travelId)
    }
}

