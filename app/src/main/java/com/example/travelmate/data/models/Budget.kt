package com.example.travelmate.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40

enum class BudgetCategory(
    val icon: ImageVector,
    val color: Color
) {
    RESTAURANT(Icons.Default.Restaurant, Color(0xFFE74C3C)),
    TOURISM(Icons.Default.Attractions, Color(0xFF3498DB)),
    TRANSPORT(Icons.Default.DirectionsBus, Color(0xFF9B59B6)),
    ACCOMMODATION(Icons.Default.Hotel, Color(0xFFF39C12)),
    ENTERTAINMENT(Icons.Default.TheaterComedy, Color(0xFF1ABC9C)),
    SHOPPING(Icons.Default.ShoppingCart, Color(0xFFE91E63)),
    OTHER(Icons.Default.MoreHoriz, Color(0xFF95A5A6))
}

data class BudgetSummary(
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val remaining: Double = 0.0,
    val expensesByCategory: Map<BudgetCategory, Double> = emptyMap()
)

@Entity(tableName = "budget_items")
@TypeConverters(com.example.travelmate.data.room.Converters::class)
data class BudgetItem(
    @PrimaryKey val id: String,
    val travelId: String,
    val title: String,
    val amount: Double,
    val category: BudgetCategory,
    val paidByUserId: String,
    val sharedWithUserIds: List<String> = emptyList(),
    val date: Long = System.currentTimeMillis(),
    val description: String? = null,
    val imageUrl: String? = null
)