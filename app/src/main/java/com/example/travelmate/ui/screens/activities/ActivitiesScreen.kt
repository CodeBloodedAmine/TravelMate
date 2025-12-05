package com.example.travelmate.ui.screens.activities

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
import com.example.travelmate.data.models.Activity
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.ActivityViewModel
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.util.SessionManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivitiesScreen(
    onNavigateToActivityDetail: (String) -> Unit
) {
    val viewModel: ActivityViewModel = travelMateViewModel()
    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadAllActivities()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Activités",
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
        } else if (activities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Aucune activité",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Les activités apparaîtront ici",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = { onNavigateToActivityDetail(activity.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    activity: Activity,
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
                text = activity.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Turquoise40
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            activity.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = dateFormat.format(Date(activity.date)),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    activity.location?.let {
                        Text(
                            text = "📍 $it",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                if (activity.cost > 0) {
                    Text(
                        text = "€${activity.cost}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Turquoise40
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Surface(
                shape = MaterialTheme.shapes.small,
                color = Turquoise40.copy(alpha = 0.1f)
            ) {
                Text(
                    text = activity.category.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = Turquoise40
                )
            }
        }
    }
}
