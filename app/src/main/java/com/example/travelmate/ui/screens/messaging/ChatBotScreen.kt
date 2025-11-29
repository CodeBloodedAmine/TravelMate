package com.example.travelmate.ui.screens.messaging

import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    onNavigateBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Initial bot message
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    id = "0",
                    content = "Bonjour ! Je suis votre assistant IA TravelMate. Comment puis-je vous aider à planifier votre voyage ?",
                    isFromBot = true
                )
            )
        }
    }
    
    fun generateBotResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()
        
        return when {
            lowerMessage.contains("budget") || lowerMessage.contains("coût") || lowerMessage.contains("prix") -> {
                "Pour gérer votre budget, je recommande de :\n" +
                "• Définir un budget total pour votre voyage\n" +
                "• Catégoriser vos dépenses (hébergement, nourriture, transport, activités)\n" +
                "• Suivre vos dépenses en temps réel dans l'onglet Budget\n" +
                "• Partager les coûts équitablement entre les participants"
            }
            lowerMessage.contains("activité") || lowerMessage.contains("que faire") || lowerMessage.contains("visiter") -> {
                "Voici quelques suggestions d'activités :\n" +
                "• Visites culturelles et musées\n" +
                "• Activités de plein air (randonnée, plage, sports)\n" +
                "• Restaurants et gastronomie locale\n" +
                "• Shopping et marchés locaux\n" +
                "• Événements et festivals\n\n" +
                "Vous pouvez créer des activités dans l'onglet Activités et les assigner aux participants !"
            }
            lowerMessage.contains("voyage") || lowerMessage.contains("planifier") || lowerMessage.contains("organiser") -> {
                "Pour bien organiser votre voyage :\n" +
                "1. Créez un nouveau voyage avec destination et dates\n" +
                "2. Invitez vos amis ou famille comme participants\n" +
                "3. Planifiez vos activités et votre itinéraire\n" +
                "4. Gérez votre budget et suivez vos dépenses\n" +
                "5. Communiquez via la messagerie de groupe\n\n" +
                "Besoin d'aide pour une étape spécifique ?"
            }
            lowerMessage.contains("bonjour") || lowerMessage.contains("salut") || lowerMessage.contains("hello") -> {
                "Bonjour ! Je suis là pour vous aider à planifier votre voyage parfait. Que souhaitez-vous savoir ?"
            }
            lowerMessage.contains("merci") || lowerMessage.contains("thanks") -> {
                "De rien ! N'hésitez pas si vous avez d'autres questions sur TravelMate. Bon voyage ! 🌍✈️"
            }
            else -> {
                "Je comprends votre question. Voici quelques conseils généraux :\n" +
                "• Utilisez l'onglet Voyages pour créer et gérer vos voyages\n" +
                "• L'onglet Activités vous permet de planifier vos sorties\n" +
                "• Le Budget vous aide à suivre vos dépenses\n" +
                "• La Messagerie permet de communiquer avec votre groupe\n\n" +
                "Avez-vous une question plus spécifique sur l'une de ces fonctionnalités ?"
            }
        }
    }
    
    fun sendMessage() {
        if (messageText.isNotBlank()) {
            val userMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = messageText,
                isFromBot = false
            )
            messages.add(userMessage)
            val currentMessage = messageText
            messageText = ""
            isLoading = true
            
            // Simulate AI response delay
            scope.launch { // <<< Change this line
                delay(1000) // Simulate processing time
                val botResponse = generateBotResponse(currentMessage)
                messages.add(
                    ChatMessage(
                        id = (System.currentTimeMillis() + 1).toString(),
                        content = botResponse,
                        isFromBot = true
                    )
                )
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🤖")
                        Text("Chat Bot IA")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange40,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                reverseLayout = true
            ) {
                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Turquoise40
                                    )
                                    Text(
                                        text = "Réflexion...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                items(messages.reversed()) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.isFromBot) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.isFromBot) 
                                    MaterialTheme.colorScheme.surfaceVariant 
                                else 
                                    Orange40
                            ),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = message.content,
                                modifier = Modifier.padding(12.dp),
                                color = if (message.isFromBot) 
                                    MaterialTheme.colorScheme.onSurface 
                                else 
                                    MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Posez votre question...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    enabled = !isLoading
                )
                
                IconButton(
                    onClick = { sendMessage() },
                    modifier = Modifier.size(56.dp),
                    enabled = !isLoading && messageText.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Orange40,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Envoyer"
                    )
                }
            }
        }
    }
}
