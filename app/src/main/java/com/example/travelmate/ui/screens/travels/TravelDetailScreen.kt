package com.example.travelmate.ui.screens.travels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.travelmate.ui.viewmodel.ActivityViewModel
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelDetailScreen(
    travelId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCreateActivity: (String) -> Unit
) {
    val travelViewModel: TravelViewModel = travelMateViewModel()
    val activityViewModel: ActivityViewModel = travelMateViewModel()
    
    val travelState = travelViewModel.getTravelById(travelId).collectAsState()
    val travel = travelState.value
    
    val activities by activityViewModel.activities.collectAsState()
    val isLoadingActivities by activityViewModel.isLoading.collectAsState()
    
    LaunchedEffect(travelId) {
        activityViewModel.loadActivitiesByTravel(travelId)
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(travel?.title ?: "Détails du voyage") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Turquoise40,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (travel != null) {
                FloatingActionButton(
                    onClick = { onNavigateToCreateActivity(travel.id) },
                    containerColor = Orange40
                ) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        if (travel == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = Turquoise40)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                                text = travel.destination,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Turquoise40
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "${dateFormat.format(Date(travel.startDate))} - ${dateFormat.format(Date(travel.endDate))}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            travel.description?.let {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Budget",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Dépensé",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "€${travel.spentAmount}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Orange40
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Budget total",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "€${travel.budget}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            val progressValue = (travel.spentAmount / travel.budget).toFloat().coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progressValue },
                                modifier = Modifier.fillMaxWidth(),
                                color = Turquoise40
                            )
                        }
                    }
                }
                
                item {
                    Text(
                        text = "Participants (${travel.participantIds.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Text(
                        text = "Activités",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (isLoadingActivities) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Turquoise40
                        )
                    }
                } else if (activities.isEmpty()) {
                    item {
                        Text(
                            text = "Aucune activité planifiée",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                } else {
                    items(activities) { activity ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = activity.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                activity.description?.let {
                                    Text(
                                        text = it,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                if (activity.location != null) {
                                    Text(
                                        text = "📍 ${activity.location}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
