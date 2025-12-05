package com.example.travelmate.ui.navigation

sealed class Screen(val route: String) {
    // Authentication
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Main Navigation
    object Home : Screen("home")
    object Travels : Screen("travels")
    object Activities : Screen("activities")
    object Budget : Screen("budget")
    object Messaging : Screen("messaging")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    
    // Detail Screens
    object TravelDetail : Screen("travel_detail/{travelId}") {
        fun createRoute(travelId: String) = "travel_detail/$travelId"
    }
    
    object ActivityDetail : Screen("activity_detail/{travelId}") {
        fun createRoute(travelId: String) = "activity_detail/$travelId"
    }
    
    object CreateTravel : Screen("create_travel")
    object CreateActivity : Screen("create_activity/{travelId}") {
        fun createRoute(travelId: String) = "create_activity/$travelId"
    }
    
    object ChatDetail : Screen("chat_detail/{travelId}") {
        fun createRoute(travelId: String) = "chat_detail/$travelId"
    }
    
    object ChatBot : Screen("chat_bot")
}

