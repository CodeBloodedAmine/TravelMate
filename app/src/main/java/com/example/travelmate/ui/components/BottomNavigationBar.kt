package com.example.travelmate.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.travelmate.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Accueil", modifier = Modifier.size(24.dp)) },
            label = { Text("Accueil") },
            selected = currentRoute == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Flight, contentDescription = "Voyages", modifier = Modifier.size(24.dp)) },
            label = { Text("Voyages") },
            selected = currentRoute == Screen.Travels.route,
            onClick = { onNavigate(Screen.Travels.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Event, contentDescription = "Activités", modifier = Modifier.size(24.dp)) },
            label = { Text("Activités") },
            selected = currentRoute == Screen.Activities.route,
            onClick = { onNavigate(Screen.Activities.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budget", modifier = Modifier.size(24.dp)) },
            label = { Text("Budget") },
            selected = currentRoute == Screen.Budget.route,
            onClick = { onNavigate(Screen.Budget.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Messagerie", modifier = Modifier.size(24.dp)) },
            label = { Text("Messagerie") },
            selected = currentRoute == Screen.Messaging.route,
            onClick = { onNavigate(Screen.Messaging.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications", modifier = Modifier.size(24.dp)) },
            label = { Text("Notifications") },
            selected = currentRoute == Screen.Notifications.route,
            onClick = { onNavigate(Screen.Notifications.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil", modifier = Modifier.size(24.dp)) },
            label = { Text("Profil") },
            selected = currentRoute == Screen.Profile.route,
            onClick = { onNavigate(Screen.Profile.route) }
        )
    }
}

