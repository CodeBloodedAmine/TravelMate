package com.example.travelmate.ui.screens.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.data.models.BudgetItem
import com.example.travelmate.data.models.BudgetCategory
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.BudgetViewModel
import com.example.travelmate.ui.viewmodel.TravelViewModel
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.util.SessionManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BudgetScreen() {
    val budgetViewModel: BudgetViewModel = travelMateViewModel()
    val travelViewModel: TravelViewModel = travelMateViewModel()
    
    val travels by travelViewModel.travels.collectAsState()
    val budgetItems by budgetViewModel.budgetItems.collectAsState()
    val budgetSummary by budgetViewModel.budgetSummary.collectAsState()
    val isLoading by budgetViewModel.isLoading.collectAsState()
    
    // Load first travel's budget if available
    LaunchedEffect(travels) {
        if (travels.isNotEmpty() && budgetItems.isEmpty()) {
            budgetViewModel.loadBudgetItems(travels.first().id)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Budget",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Turquoise40,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = Turquoise40)
            }
        } else if (budgetSummary == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Aucun budget disponible",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Créez un voyage pour commencer",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            val summary = budgetSummary!!
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Turquoise40.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Budget total",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "€${String.format("%.2f", summary.totalBudget)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Turquoise40
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Dépensé",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "€${String.format("%.2f", summary.totalSpent)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Orange40
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Restant",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "€${String.format("%.2f", summary.remaining)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (summary.remaining < 0) Orange40 else Turquoise40
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { (summary.totalSpent / summary.totalBudget).coerceIn(0.0, 1.0).toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = Turquoise40
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Dépenses par catégorie",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (summary.expensesByCategory.isEmpty()) {
                Text(
                    text = "Aucune dépense enregistrée",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                summary.expensesByCategory.forEach { (category, amount) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category.name,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "€${String.format("%.2f", amount)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Turquoise40
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Dernières dépenses",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (budgetItems.isEmpty()) {
                Text(
                    text = "Aucune dépense",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(budgetItems.take(10)) { item ->
                        BudgetItemCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItemCard(item: BudgetItem) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                item.description?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    text = "${dateFormat.format(Date(item.date))} • ${item.category.name}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "€${String.format("%.2f", item.amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Orange40
            )
        }
    }
}
