package com.example.travelmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.travelmate.ui.screens.auth.LoginScreen
import com.example.travelmate.ui.screens.auth.RegisterScreen
import com.example.travelmate.ui.screens.home.HomeScreen
import com.example.travelmate.ui.screens.travels.TravelsScreen
import com.example.travelmate.ui.screens.travels.TravelDetailScreen
import com.example.travelmate.ui.screens.travels.CreateTravelScreen
import com.example.travelmate.ui.screens.activities.ActivitiesScreen
import com.example.travelmate.ui.screens.activities.ActivityDetailScreen
import com.example.travelmate.ui.screens.activities.CreateActivityScreen
import com.example.travelmate.ui.screens.budget.BudgetScreen
import com.example.travelmate.ui.screens.messaging.MessagingScreen
import com.example.travelmate.ui.screens.messaging.ChatDetailScreen
import com.example.travelmate.ui.screens.messaging.ChatBotScreen
import com.example.travelmate.ui.screens.notifications.NotificationsScreen
import com.example.travelmate.ui.screens.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) { 
                    popUpTo(Screen.Login.route) { inclusive = true }
                }},
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }},
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTravels = { navController.navigate(Screen.Travels.route) },
                onNavigateToTravelDetail = { travelId -> 
                    navController.navigate(Screen.TravelDetail.createRoute(travelId))
                },
                onNavigateToSearch = { navController.navigate(Screen.Travels.route) },
                onNavigateToCreateTravel = { navController.navigate(Screen.CreateTravel.route) }
            )
        }
        
        composable(Screen.Travels.route) {
            TravelsScreen(
                onNavigateToTravelDetail = { travelId ->
                    navController.navigate(Screen.TravelDetail.createRoute(travelId))
                },
                onNavigateToCreateTravel = { navController.navigate(Screen.CreateTravel.route) }
            )
        }
        
        composable(
            route = Screen.TravelDetail.route,
            arguments = listOf(navArgument("travelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
            TravelDetailScreen(
                travelId = travelId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateActivity = { travelId ->
                    navController.navigate(Screen.CreateActivity.createRoute(travelId))
                }
            )
        }
        
        composable(Screen.CreateTravel.route) {
            CreateTravelScreen(
                onNavigateBack = { navController.popBackStack() },
                onTravelCreated = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Activities.route) {
            ActivitiesScreen(
                onNavigateToActivityDetail = { travelId ->
                    navController.navigate(Screen.ActivityDetail.createRoute(travelId))
                }
            )
        }
        
        composable(
            route = Screen.ActivityDetail.route,
            arguments = listOf(navArgument("travelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
            ActivityDetailScreen(
                travelId = travelId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.CreateActivity.route,
            arguments = listOf(navArgument("travelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
            CreateActivityScreen(
                travelId = travelId,
                onNavigateBack = { navController.popBackStack() },
                onActivityCreated = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Budget.route) {
            BudgetScreen()
        }
        
        composable(Screen.Messaging.route) {
            MessagingScreen(
                onNavigateToChatDetail = { travelId, travelTitle ->
                    navController.navigate(Screen.ChatDetail.createRoute(travelId))
                },
                onNavigateToChatBot = { navController.navigate(Screen.ChatBot.route) },
                onNavigateToNewChat = { /* TODO: Navigate to new chat */ }
            )
        }
        
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("travelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
            ChatDetailScreen(
                travelId = travelId,
                travelTitle = "Chat de groupe",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ChatBot.route) {
            ChatBotScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = { navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }},
                onNavigateToSettings = { /* Navigate to settings */ },
                onNavigateToHelp = { /* Navigate to help */ },
                onNavigateToAbout = { /* Navigate to about */ }
            )
        }
    }
}

