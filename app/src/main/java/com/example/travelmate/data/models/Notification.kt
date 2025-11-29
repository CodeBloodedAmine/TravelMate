package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val relatedTravelId: String? = null,
    val relatedActivityId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType {
    TRAVEL_INVITATION,
    ACTIVITY_REMINDER,
    BUDGET_UPDATE,
    NEW_MESSAGE,
    TRAVEL_UPDATED,
    ACTIVITY_CREATED,
    PARTICIPANT_JOINED,
    OTHER
}

