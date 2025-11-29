package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val travelId: String? = null, // null if private message
    val senderId: String,
    val receiverId: String? = null, // null if group message
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val imageUrl: String? = null
)

enum class MessageType {
    TEXT,
    IMAGE,
    SYSTEM // For notifications like "User joined travel"
}

data class ChatBotMessage(
    val id: String,
    val userId: String,
    val content: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestions: List<String>? = null // Suggested responses for bot messages
)

