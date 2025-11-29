package com.example.travelmate.ui.screens.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.travelmate.data.models.ActivityCategory
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.ActivityViewModel
import com.example.travelmate.ui.theme.Turquoise40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    travelId: String,
    onNavigateBack: () -> Unit,
    onActivityCreated: () -> Unit
) {
    val viewModel: ActivityViewModel = travelMateViewModel()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.OTHER) }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle activité") },
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
                label = { Text("Titre de l'activité *") },
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
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Heure (ex: 14:00)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Coût (€)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Text(
                text = "Catégorie",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            val categories = ActivityCategory.entries.toTypedArray()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories.size) { index ->
                    FilterChip(
                        selected = selectedCategory == categories[index],
                        onClick = { selectedCategory = categories[index] },
                        label = { Text(categories[index].name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.createActivity(
                            travelId = travelId,
                            title = title,
                            description = description.ifBlank { null },
                            date = date,
                            time = time.ifBlank { null },
                            location = location.ifBlank { null },
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            category = selectedCategory
                        )
                        onActivityCreated()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Turquoise40
                ),
                enabled = title.isNotBlank()
            ) {
                Text("Créer l'activité", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
