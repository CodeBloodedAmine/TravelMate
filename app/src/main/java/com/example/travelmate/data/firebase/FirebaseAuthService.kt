package com.example.travelmate.data.firebase

import com.example.travelmate.data.models.User
import com.example.travelmate.data.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUp(email: String, password: String, name: String, role: UserRole = UserRole.PARTICIPANT): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            
            // Create user profile in Realtime Database
            val userProfile = User(
                id = user.uid,
                email = email,
                name = name,
                role = role,
                createdAt = System.currentTimeMillis()
            )
            
            database.reference.child("users").child(user.uid).setValue(userProfile).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            database.reference.child("users").child(user.id).setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(userId: String): Result<User?> {
        return try {
            val snapshot = database.reference.child("users").child(userId).get().await()
            val user = snapshot.getValue(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

