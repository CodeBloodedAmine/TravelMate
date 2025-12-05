package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Message
import com.example.travelmate.data.room.MessageDao
import kotlinx.coroutines.flow.Flow

class MessageRepository(private val messageDao: MessageDao) {
    
    // ✅ Get messages for a travel (GROUP CHAT)
    fun getMessagesByTravelId(travelId: String): Flow<List<Message>> = 
        messageDao.getMessagesByTravelId(travelId)
    
    // ✅ Get a single message
    fun getMessageById(messageId: String): Flow<Message?> = 
        messageDao.getMessageById(messageId)
    
    // ✅ Insert a message
    suspend fun insertMessage(message: Message) = 
        messageDao.insertMessage(message)
    
    // ✅ Insert multiple messages
    suspend fun insertMessages(messages: List<Message>) = 
        messageDao.insertMessages(messages)
    
    // ✅ Update a message
    suspend fun updateMessage(message: Message) = 
        messageDao.updateMessage(message)
    
    // ✅ Mark a message as read
    suspend fun markAsRead(messageId: String) = 
        messageDao.markAsRead(messageId)
    
    // ✅ Delete a message
    suspend fun deleteMessage(messageId: String) = 
        messageDao.deleteMessage(messageId)
    
    // ✅ Get unread count for a travel
    fun getUnreadCountByTravelId(travelId: String): Flow<Int> = 
        messageDao.getUnreadCountByTravelId(travelId)
}

