package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    PARTICIPANT,
    ORGANISER
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    val email: String = "",
    val name: String = "",
    val profilePictureUrl: String? = null,
    val role: UserRole = UserRole.PARTICIPANT,
    val phoneNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)