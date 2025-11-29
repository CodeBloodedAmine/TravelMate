package com.example.travelmate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "budget_items")
@TypeConverters(com.example.travelmate.data.room.Converters::class)
data class BudgetItem(
    @PrimaryKey val id: String,
    val travelId: String,
    val title: String,
    val amount: Double,
    val category: BudgetCategory,
    val paidByUserId: String,
    val sharedWithUserIdsJson: String = "[]", // JSON string for List<String>
    val date: Long = System.currentTimeMillis(),
    val description: String? = null,
    val imageUrl: String? = null
) {
    // Helper property (not stored in DB)
    val sharedWithUserIds: List<String>
        get() = try {
            com.google.gson.Gson().fromJson(sharedWithUserIdsJson, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
}

enum class BudgetCategory {
    ACCOMMODATION,
    FOOD,
    TRANSPORT,
    ACTIVITIES,
    SHOPPING,
    EMERGENCY,
    OTHER
}

data class BudgetSummary(
    val travelId: String,
    val totalBudget: Double,
    val totalSpent: Double,
    val remaining: Double,
    val expensesByCategory: Map<BudgetCategory, Double>,
    val expensesByUser: Map<String, Double>
)

