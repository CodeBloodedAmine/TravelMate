package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelmate.TravelMateApplication
import com.example.travelmate.data.repository.*

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val travelRepository: TravelRepository,
    private val activityRepository: ActivityRepository,
    private val budgetRepository: BudgetRepository,
    private val messageRepository: MessageRepository,
    private val notificationRepository: NotificationRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(travelRepository) as T
            }
            modelClass.isAssignableFrom(TravelViewModel::class.java) -> {
                TravelViewModel(travelRepository) as T
            }
            modelClass.isAssignableFrom(ActivityViewModel::class.java) -> {
                ActivityViewModel(activityRepository) as T
            }
            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> {
                BudgetViewModel(budgetRepository, travelRepository) as T
            }
            modelClass.isAssignableFrom(MessageViewModel::class.java) -> {
                MessageViewModel(messageRepository) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(notificationRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    
    companion object {
        fun create(context: android.content.Context): ViewModelFactory {
            val database = (context.applicationContext as TravelMateApplication).database
            return ViewModelFactory(
                userRepository = UserRepository(database.userDao()),
                travelRepository = TravelRepository(database.travelDao()),
                activityRepository = ActivityRepository(database.activityDao()),
                budgetRepository = BudgetRepository(database.budgetDao()),
                messageRepository = MessageRepository(database.messageDao()),
                notificationRepository = NotificationRepository(database.notificationDao())
            )
        }
    }
}

