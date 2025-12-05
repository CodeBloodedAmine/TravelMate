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
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.ui.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesListScreen(
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
    var activityCategory by remember { mutableStateOf("OTHER") }
    
    LaunchedEffect(travelId) {
        viewModel.loadActivitiesByTravel(travelId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activités") },
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
        if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "❌ Erreur: ${error}",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Turquoise40)
            }
        } else if (activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Aucune activité",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ajoutez une première activité!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityCard(activity)
                }
            }
        }
    }
    
    // Add Activity Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Ajouter une activité") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = activityTitle,
                        onValueChange = { activityTitle = it },
                        label = { Text("Titre") },
                        modifier = Modifier.fillMaxWidth()
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
                        label = { Text("Lieu") },
                        modifier = Modifier.fillMaxWidth()
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
                                description = activityDescription.takeIf { it.isNotBlank() },
                                date = System.currentTimeMillis(),
                                time = null,
                                location = activityLocation,
                                category = ActivityCategory.OTHER
                            )
                            activityTitle = ""
                            activityDescription = ""
                            activityLocation = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Ajouter")
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
