package com.example.travelmate.ui.screens.travels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.data.models.Travel
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.TravelViewModel
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.util.SessionManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TravelsScreen(
    onNavigateToTravelDetail: (String) -> Unit,
    onNavigateToCreateTravel: () -> Unit
) {
    val viewModel: TravelViewModel = travelMateViewModel()
    val travels by viewModel.travels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Get user role for permission check
    val userRole = SessionManager.getUserRole()
    val isOrganiser = userRole?.name == "ORGANISER"

    LaunchedEffect(Unit) {
        viewModel.loadTravels()
    }

    Scaffold(
        floatingActionButton = {
            if (isOrganiser) {
                FloatingActionButton(
                    onClick = onNavigateToCreateTravel,
                    containerColor = Turquoise40,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nouveau voyage",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Voyages",
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
                            text = "Aucun voyage",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Créez votre premier voyage !",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(travels) { travel ->
                        TravelCard(
                            travel = travel,
                            onClick = { onNavigateToTravelDetail(travel.id) }
                        )
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
    val viewModel: TravelViewModel = travelMateViewModel()
    val currentUserId = SessionManager.getCurrentUserId()
    val isParticipant = travel.participantIds.contains(currentUserId)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var isJoining by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = travel.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Turquoise40
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            travel.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
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
            
            LinearProgressIndicator(
                progress = { (travel.spentAmount / travel.budget).coerceIn(0.0, 1.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = Turquoise40,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action button
            if (isParticipant) {
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Turquoise40)
                ) {
                    Text("Voir le voyage")
                }
            } else {
                Button(
                    onClick = {
                        isJoining = true
                        viewModel.participateInTravel(travel.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isJoining,
                    colors = ButtonDefaults.buttonColors(containerColor = Orange40)
                ) {
                    Text(if (isJoining) "Rejoindre..." else "Rejoindre le voyage")
                }
            }
        }
    }
}
