package com.example.travelmate.ui.screens.travels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.TravelViewModel
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.util.SessionManager
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTravelScreen(
    onNavigateBack: () -> Unit,
    onTravelCreated: () -> Unit
) {
    val viewModel: TravelViewModel = travelMateViewModel()
    
    // Check user role
    val userRole = SessionManager.getUserRole()
    val isOrganiser = userRole?.name == "ORGANISER"
    
    if (!isOrganiser) {
        // Show permission denied dialog
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            AlertDialog(
                onDismissRequest = onNavigateBack,
                title = { Text("Accès refusé") },
                text = { Text("Seul l'organisateur du voyage peut créer de nouveaux voyages.") },
                confirmButton = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Retour")
                    }
                }
            )
        }
        return
    }
    
    var title by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau voyage") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre du voyage *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )
            
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget (€) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Text(
                text = "Dates (par défaut: aujourd'hui + 7 jours)",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val budgetValue = budget.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && destination.isNotBlank() && budgetValue > 0) {
                        viewModel.createTravel(
                            title = title,
                            destination = destination,
                            description = description.ifBlank { null },
                            startDate = startDate,
                            endDate = endDate,
                            budget = budgetValue
                        )
                        onTravelCreated()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Turquoise40
                ),
                enabled = title.isNotBlank() && destination.isNotBlank() && budget.toDoubleOrNull() != null
            ) {
                Text("Créer le voyage", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
