package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "activities")
@TypeConverters(com.example.travelmate.data.room.Converters::class)
data class Activity(
    @PrimaryKey val id: String,
    val travelId: String,
    val title: String,
    val description: String? = null,
    val date: Long,
    val time: String? = null,
    val location: String? = null,
    val assignedParticipantIds: List<String> = emptyList(),
    val cost: Double = 0.0,
    val category: ActivityCategory = ActivityCategory.OTHER,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

