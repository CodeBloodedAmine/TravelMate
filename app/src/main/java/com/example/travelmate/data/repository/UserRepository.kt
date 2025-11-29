package com.example.travelmate.data.repository

import com.example.travelmate.data.models.User
import com.example.travelmate.data.room.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getUserById(userId: String): Flow<User?> = userDao.getUserById(userId)
    
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
}

