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
    val participantIdsJson: String = "[]", // JSON string for List<String>
    val budget: Double = 0.0,
    val spentAmount: Double = 0.0,
    val imageUrl: String? = null,
    val itineraryJson: String = "[]", // JSON string for List<ItineraryItem>
    val createdAt: Long = System.currentTimeMillis()
) {
    // Helper properties (not stored in DB)
    val participantIds: List<String>
        get() = try {
            com.google.gson.Gson().fromJson(participantIdsJson, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    
    val itinerary: List<ItineraryItem>
        get() = try {
            com.google.gson.Gson().fromJson(itineraryJson, object : com.google.gson.reflect.TypeToken<List<ItineraryItem>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
}

data class ItineraryItem(
    val id: String,
    val date: Long,
    val time: String? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null
)

