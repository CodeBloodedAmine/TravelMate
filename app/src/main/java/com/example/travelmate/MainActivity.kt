package com.example.travelmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.travelmate.ui.components.BottomNavigationBar
import com.example.travelmate.ui.navigation.NavGraph
import com.example.travelmate.ui.navigation.Screen
import com.example.travelmate.ui.theme.TravelMateTheme
import com.example.travelmate.util.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TravelMateApp()
                }
            }
        }
    }
}

@Composable
fun TravelMateApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Check if user is logged in - verify both flag and userId
    val isLoggedIn = SessionManager.isLoggedIn() && SessionManager.getCurrentUserId() != null
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    
    // Navigate to login if not authenticated when on protected routes
    LaunchedEffect(currentRoute, isLoggedIn) {
        val protectedRoutes = listOf(
            Screen.Home.route,
            Screen.Travels.route,
            Screen.Activities.route,
            Screen.Budget.route,
            Screen.Messaging.route,
            Screen.Notifications.route,
            Screen.Profile.route
        )
        
        if (!isLoggedIn && currentRoute in protectedRoutes) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    // List of routes that should show bottom navigation
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Travels.route,
        Screen.Activities.route,
        Screen.Budget.route,
        Screen.Messaging.route,
        Screen.Notifications.route,
        Screen.Profile.route
    )
    
    val showBottomNav = currentRoute in bottomNavRoutes && isLoggedIn
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        // Handle navigation with proper backstack management
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                // Pop up to home but don't include it to keep it in backstack
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavGraph(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}
