package com.example.travelmate.data.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.client.generativeai.type.text

object AIService {
    // Initialize the Generative Model with Gemini API
    // Get your API key from: https://aistudio.google.com/app/apikey
    private const val GEMINI_API_KEY = "AIzaSyA2J3y7vvirO83P4XAyn40TSun3DafQltY"  // Replace with your actual API key
    
    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            },
            systemInstruction = content {
                text(
                    """You are a helpful travel assistant for the TravelMate app.
                    |You provide advice about:
                    |- Trip planning and itineraries
                    |- Travel destinations and attractions
                    |- Budget management for trips
                    |- Packing tips and travel essentials
                    |- Local customs and cultural tips
                    |- Safety tips for travelers
                    |Keep your responses concise and friendly. Use emojis to make it engaging.
                    |Always include practical, actionable advice.""".trimMargin()
                )
            }
        )
    }
    
    /**
     * Send a message to Gemini and get a response
     * @param message The user's message
     * @return The AI response or error message
     */
    suspend fun sendMessage(message: String): String {
        return try {
            Log.d("AIService", "🤖 Sending message to Gemini: $message")
            
            val response = model.generateContent(
                content { text(message) }
            )
            val aiResponse = response.text ?: "No response from AI"
            
            Log.d("AIService", "✅ Received response from Gemini: $aiResponse")
            aiResponse
        } catch (e: Exception) {
            Log.e("AIService", "❌ Error calling Gemini API: ${e.message}", e)
            "Sorry, I couldn't process your request. Please try again. (${e.message})"
        }
    }
    
    /**
     * Generate travel tips based on destination
     */
    suspend fun generateTravelTips(destination: String, tripDays: Int): String {
        return try {
            val prompt = "Give me 3 key travel tips for a $tripDays day trip to $destination, including one budgeting tip and one local culture tip."
            Log.d("AIService", "🌍 Generating travel tips for: $destination ($tripDays days)")
            sendMessage(prompt)
        } catch (e: Exception) {
            Log.e("AIService", "❌ Error generating travel tips: ${e.message}")
            "Unable to generate travel tips at this moment."
        }
    }
    
    /**
     * Generate a packing list based on destination and trip type
     */
    suspend fun generatePackingList(destination: String, tripType: String): String {
        return try {
            val prompt = "Create a brief packing list for a $tripType trip to $destination. Focus on essentials and climate-appropriate items."
            Log.d("AIService", "🧳 Generating packing list for: $destination ($tripType)")
            sendMessage(prompt)
        } catch (e: Exception) {
            Log.e("AIService", "❌ Error generating packing list: ${e.message}")
            "Unable to generate packing list at this moment."
        }
    }
    
    /**
     * Get budget advice for a trip
     */
    suspend fun getBudgetAdvice(destination: String, budget: Double, days: Int): String {
        return try {
            val prompt = "I have $${String.format("%.2f", budget)} for a $days-day trip to $destination. How should I budget this? Include suggestions for accommodation, food, activities, and transportation."
            Log.d("AIService", "💰 Getting budget advice for: $destination ($$budget for $days days)")
            sendMessage(prompt)
        } catch (e: Exception) {
            Log.e("AIService", "❌ Error getting budget advice: ${e.message}")
            "Unable to provide budget advice at this moment."
        }
    }
    
    /**
     * Test the connection and model
     */
    suspend fun testConnection(): String {
        return try {
            Log.d("AIService", "🔌 Testing Gemini API connection...")
            val response = sendMessage("Hello! Can you confirm you're working by saying hello back?")
            Log.d("AIService", "✅ API connection test successful")
            response
        } catch (e: Exception) {
            Log.e("AIService", "❌ API connection test failed: ${e.message}")
            "API Connection Failed: ${e.message}"
        }
    }
}
