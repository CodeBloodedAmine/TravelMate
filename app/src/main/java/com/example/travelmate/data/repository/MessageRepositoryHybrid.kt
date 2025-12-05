package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Message
import com.example.travelmate.data.room.MessageDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class MessageRepositoryHybrid(
    private val messageDao: MessageDao,
    private val firebaseService: FirebaseRealtimeService,
    private val networkMonitor: NetworkMonitor
) {
    
    fun getMessagesByTravelId(travelId: String): Flow<List<Message>> {
        return if (networkMonitor.isNetworkAvailable()) {
            firebaseService.listenToMessagesByTravelId(travelId)
                .onEach { messages ->
                    messages.forEach { messageDao.insertMessage(it) }
                }
        } else {
            messageDao.getMessagesByTravelId(travelId)
        }
    }
    
    fun sendMessage(message: Message): Flow<Result<Unit>> = flow {
        emit(Result.loading())
        
        try {
            messageDao.insertMessage(message)
            
            if (networkMonitor.isNetworkAvailable()) {
                firebaseService.saveMessage(message) { result ->
                    result.onSuccess {
                        // Success
                    }.onFailure { exception ->
                        // Handle error silently
                    }
                }
                kotlinx.coroutines.delay(500)
            }
            
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun markAsRead(messageId: String) {
        try {
            messageDao.markAsRead(messageId)
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
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

