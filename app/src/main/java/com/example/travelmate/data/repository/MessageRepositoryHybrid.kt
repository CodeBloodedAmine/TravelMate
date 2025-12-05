package com.example.travelmate.data.repository

import android.util.Log
import com.example.travelmate.data.models.Message
import com.example.travelmate.data.room.MessageDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await

class MessageRepositoryHybrid(
    private val messageDao: MessageDao,
    private val firebaseService: FirebaseRealtimeService,
    private val networkMonitor: NetworkMonitor
) {
    
    // ✅ Get messages for a travel (group chat)
    fun getMessagesByTravelId(travelId: String): Flow<List<Message>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Fetch from Firebase at: travels/{travelId}/messages
            firebaseService.listenToMessagesByTravelId(travelId)
                .onEach { messages ->
                    // Save to local Room database for offline access
                    messages.forEach { messageDao.insertMessage(it) }
                }
        } else {
            // Use local Room database if offline
            messageDao.getMessagesByTravelId(travelId)
        }
    }
    
    // ✅ Send a message
    fun sendMessage(message: Message): Flow<Result<Unit>> = flow {
        emit(Result.loading())
        
        try {
            // 1. Save to local database first (for immediate UI update and offline)
            messageDao.insertMessage(message)
            Log.d("MessageRepository", "Message saved to local database")
            
            // 2. Sync with Firebase at: travels/{travelId}/messages/{messageId}
            if (networkMonitor.isNetworkAvailable()) {
                Log.d("MessageRepository", "Attempting to save message to Firebase at: travels/${message.travelId}/messages/${message.id}")
                firebaseService.saveMessage(message) { result ->
                    result.onSuccess {
                        Log.d("MessageRepository", "✅ Message saved to Firebase")
                    }.onFailure { exception ->
                        Log.e("MessageRepository", "❌ Firebase sync failed: ${exception.message}")
                        exception.printStackTrace()
                    }
                }
                // Small delay to ensure Firebase callback completes
                kotlinx.coroutines.delay(500)
            } else {
                Log.w("MessageRepository", "⚠️ No network - message saved locally only")
            }
            
            emit(Result.success(Unit))
        } catch (e: Exception) {
            Log.e("MessageRepository", "❌ Error sending message: ${e.message}")
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
    
    // ✅ Mark a message as read
    suspend fun markAsRead(messageId: String) {
        try {
            messageDao.markAsRead(messageId)
        } catch (e: Exception) {
            Log.e("MessageRepository", "Error marking message as read: ${e.message}")
        }
    }
    
    // ✅ Insert a message (used for local saving)
    suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(message)
    }
}

// ✅ Result wrapper for async operations
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure<T>(val exception: Exception) : Result<T>()
    class Loading<T> : Result<T>()
    
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> Failure(exception)
        is Loading -> Loading()
    }
    
    fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) block(data)
        return this
    }
    
    fun onFailure(block: (Exception) -> Unit): Result<T> {
        if (this is Failure) block(exception)
        return this
    }
    
    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failure(exception: Exception) = Failure<T>(exception)
    }
}

