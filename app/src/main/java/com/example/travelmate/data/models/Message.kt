package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String = "",
    val travelId: String = "",    
    val senderId: String = "",          
    val content: String = "",          
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "TEXT",  
    val read: Boolean = false
)

enum class MessageType {
    TEXT,
    IMAGE,
    SYSTEM // For notifications like "User joined travel"
}

@Entity(tableName = "chatbot_messages")
data class ChatBotMessage(
    @PrimaryKey val id: String,
    val userId: String,
    val content: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestions: List<String>? = null // Suggested responses for bot messages
)

