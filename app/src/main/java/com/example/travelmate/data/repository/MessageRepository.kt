package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Message
import com.example.travelmate.data.room.MessageDao
import kotlinx.coroutines.flow.Flow

class MessageRepository(private val messageDao: MessageDao) {
    fun getMessagesByTravel(travelId: String): Flow<List<Message>> = messageDao.getMessagesByTravel(travelId)
    
    fun getPrivateMessages(userId: String, otherUserId: String): Flow<List<Message>> = 
        messageDao.getPrivateMessages(userId, otherUserId)
    
    fun getMessageById(messageId: String): Flow<Message?> = messageDao.getMessageById(messageId)
    
    suspend fun insertMessage(message: Message) = messageDao.insertMessage(message)
    
    suspend fun updateMessage(message: Message) = messageDao.updateMessage(message)
    
    suspend fun markMessagesAsRead(travelId: String, userId: String) = messageDao.markMessagesAsRead(travelId, userId)
    
    suspend fun deleteMessage(message: Message) = messageDao.deleteMessage(message)
}

