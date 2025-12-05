package com.example.travelmate.data.room

import androidx.room.*
import com.example.travelmate.data.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    
    // ✅ Get all messages for a specific travel (GROUP CHAT)
    @Query("SELECT * FROM messages WHERE travelId = :travelId ORDER BY timestamp ASC")
    fun getMessagesByTravelId(travelId: String): Flow<List<Message>>
    
    // ✅ Get a single message by ID
    @Query("SELECT * FROM messages WHERE id = :messageId")
    fun getMessageById(messageId: String): Flow<Message?>
    
    // ✅ Insert a new message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
    
    // ✅ Insert multiple messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    // ✅ Update a message
    @Update
    suspend fun updateMessage(message: Message)
    
    // ✅ Mark a message as read
    @Query("UPDATE messages SET `read` = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: String)
    
    // ✅ Delete a message
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
    
    // ✅ Delete all messages for a travel
    @Query("DELETE FROM messages WHERE travelId = :travelId")
    suspend fun deleteMessagesByTravel(travelId: String)
    
    // ✅ Delete all messages
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
    
    // ✅ Get unread message count for a travel
    @Query("SELECT COUNT(*) FROM messages WHERE travelId = :travelId AND `read` = 0")
    fun getUnreadCountByTravelId(travelId: String): Flow<Int>
}

