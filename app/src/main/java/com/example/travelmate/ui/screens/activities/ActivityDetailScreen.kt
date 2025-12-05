package com.example.travelmate.ui.screens.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.data.models.Activity
import com.example.travelmate.data.models.ActivityCategory
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.ActivityViewModel
import com.example.travelmate.ui.theme.Turquoise40
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    travelId: String,
    onNavigateBack: () -> Unit
) {
    val viewModel: ActivityViewModel = travelMateViewModel()
    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var activityTitle by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }
    var activityLocation by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.OTHER) }
    
    LaunchedEffect(travelId) {
        viewModel.loadActivitiesByTravel(travelId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activités du voyage") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Turquoise40,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Turquoise40
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter une activité")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Error message
            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
                ) {
                    Text(
                        text = error ?: "Erreur",
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFFC62828),
                        fontSize = 13.sp
                    )
                }
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Turquoise40)
                }
            } else if (activities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📝 Aucune activité",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ajoutez une activité pour ce voyage",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activities) { activity ->
                        ActivityCard(activity)
                    }
                }
            }
        }
    }
    
    // Add Activity Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nouvelle activité") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = activityTitle,
                        onValueChange = { activityTitle = it },
                        label = { Text("Titre *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = activityDescription,
                        onValueChange = { activityDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = activityLocation,
                        onValueChange = { activityLocation = it },
                        label = { Text("Lieu *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (activityTitle.isNotBlank() && activityLocation.isNotBlank()) {
                            viewModel.createActivity(
                                travelId = travelId,
                                title = activityTitle,
                                description = activityDescription.ifBlank { null },
                                date = System.currentTimeMillis(),
                                time = null,
                                location = activityLocation,
                                cost = 0.0,
                                category = selectedCategory
                            )
                            activityTitle = ""
                            activityDescription = ""
                            activityLocation = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Créer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun ActivityCard(activity: Activity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = activity.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Turquoise40
            )
            
            if (!activity.description.isNullOrBlank()) {
                Text(
                    text = activity.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 ${activity.location}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(activity.date)),
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Turquoise40,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

