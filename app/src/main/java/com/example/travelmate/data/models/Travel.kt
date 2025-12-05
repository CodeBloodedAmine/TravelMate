package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.travelmate.data.room.Converters

@Entity(tableName = "travels")
@TypeConverters(Converters::class)
data class Travel(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val organiserId: String,
    val participantIds: List<String> = emptyList(),
    val budget: Double = 0.0,
    val spentAmount: Double = 0.0,
    val imageUrl: String? = null,
    val itinerary: List<ItineraryItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ItineraryItem(
    val id: String,
    val date: Long,
    val time: String? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null
)

