package com.example.travelmate.data.room

import androidx.room.*
import com.example.travelmate.data.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE travelId = :travelId ORDER BY timestamp ASC")
    fun getMessagesByTravel(travelId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverId = :otherUserId) OR (senderId = :otherUserId AND receiverId = :userId) ORDER BY timestamp ASC")
    fun getPrivateMessages(userId: String, otherUserId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    fun getMessageById(messageId: String): Flow<Message?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("UPDATE messages SET isRead = 1 WHERE travelId = :travelId AND receiverId = :userId AND isRead = 0")
    suspend fun markMessagesAsRead(travelId: String, userId: String)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("DELETE FROM messages WHERE travelId = :travelId")
    suspend fun deleteMessagesByTravel(travelId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}

