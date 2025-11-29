package com.example.travelmate.ui.screens.home

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
import com.example.travelmate.data.models.Travel
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.HomeViewModel
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onNavigateToTravels: () -> Unit,
    onNavigateToTravelDetail: (String) -> Unit
) {
    val viewModel: HomeViewModel = travelMateViewModel()
    val travels by viewModel.travels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTravels()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mes voyages",
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
        } else if (travels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Aucun voyage pour le moment",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Button(
                        onClick = onNavigateToTravels,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Turquoise40
                        )
                    ) {
                        Text("Voir tous les voyages")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(travels) { travel ->
                    TravelCard(
                        travel = travel,
                        onClick = { onNavigateToTravelDetail(travel.id) }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToTravels,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange40
                        )
                    ) {
                        Text("Voir tous les voyages")
                    }
                }
            }
        }
    }
}

@Composable
fun TravelCard(
    travel: Travel,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = travel.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Turquoise40
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = travel.destination,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${dateFormat.format(Date(travel.startDate))} - ${dateFormat.format(Date(travel.endDate))}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = "€${travel.spentAmount}/${travel.budget}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (travel.spentAmount > travel.budget * 0.8) Orange40 else Turquoise40
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            val progressValue = (travel.spentAmount / travel.budget).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier.fillMaxWidth(),
                color = Turquoise40,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
