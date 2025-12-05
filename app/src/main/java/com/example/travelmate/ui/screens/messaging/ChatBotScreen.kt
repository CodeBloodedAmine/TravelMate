package com.example.travelmate.ui.screens.messaging

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

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
    var showQuickReplies by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()

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

    // Auto-scroll to bottom when new messages are added
    LaunchedEffect(messages.size) {
        delay(100)
        scope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    fun generateBotResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()

        return when {
            lowerMessage.contains("budget") || lowerMessage.contains("coût") || lowerMessage.contains("prix") -> {
                "**Gestion de budget** 💰\n\nPour gérer votre budget efficacement :\n\n" +
                        "📊 **Conseils :**\n" +
                        "• Définissez un budget total réaliste\n" +
                        "• Catégorisez vos dépenses (hébergement 40%, transport 30%, activités 20%, imprévus 10%)\n" +
                        "• Utilisez l'onglet Budget pour un suivi en temps réel\n" +
                        "• Partagez équitablement entre participants\n\n" +
                        "💡 **Astuce :** Réservez 10-15% de votre budget pour les imprévus !"
            }
            lowerMessage.contains("activité") || lowerMessage.contains("que faire") || lowerMessage.contains("visiter") -> {
                "**Activités & Visites** 🗺️\n\nVoici des suggestions par catégorie :\n\n" +
                        "🏛️ **Culture :** Musées, monuments historiques, visites guidées\n" +
                        "🌳 **Nature :** Randonnées, parcs naturels, plages\n" +
                        "🎭 **Divertissement :** Spectacles, concerts, événements locaux\n" +
                        "🍽️ **Gastronomie :** Restaurants typiques, cours de cuisine, marchés\n" +
                        "🛍️ **Shopping :** Boutiques artisanales, centres commerciaux\n\n" +
                        "✨ Créez vos activités dans l'onglet Activités pour les partager avec votre groupe !"
            }
            lowerMessage.contains("voyage") || lowerMessage.contains("planifier") || lowerMessage.contains("organiser") -> {
                "**Organisation de voyage** ✈️\n\nVoici un plan étape par étape :\n\n" +
                        "1. **Destination & Dates** 🗓️ - Choisissez où et quand partir\n" +
                        "2. **Participants** 👥 - Invitez vos amis/famille\n" +
                        "3. **Budget** 💰 - Fixez et suivez vos dépenses\n" +
                        "4. **Activités** 🎯 - Planifiez votre itinéraire\n" +
                        "5. **Communication** 💬 - Échangez via la messagerie\n" +
                        "6. **Documents** 📋 - Vérifiez passeports, visas, assurances\n\n" +
                        "Je peux vous aider avec une étape spécifique !"
            }
            lowerMessage.contains("bonjour") || lowerMessage.contains("salut") || lowerMessage.contains("hello") -> {
                "Bonjour ! 👋 Je suis là pour vous aider à planifier votre voyage parfait.\n\n" +
                        "Je peux vous assister sur :\n" +
                        "• Budget et finances 💰\n" +
                        "• Activités et visites 🗺️\n" +
                        "• Organisation générale ✈️\n" +
                        "• Astuces voyage 🧳\n\n" +
                        "Que souhaitez-vous savoir ?"
            }
            lowerMessage.contains("merci") || lowerMessage.contains("thanks") -> {
                "Avec plaisir ! 🎉\n\n" +
                        "N'hésitez pas si vous avez d'autres questions. Je suis là pour vous aider à rendre votre voyage inoubliable !\n\n" +
                        "Bon voyage et à bientôt ! 🌍✈️"
            }
            lowerMessage.contains("météo") || lowerMessage.contains("temps") -> {
                "**Météo & Climat** ☀️🌧️\n\n" +
                        "Pour bien préparer vos bagages :\n" +
                        "• Consultez les prévisions 10 jours avant le départ\n" +
                        "• Adaptez vos vêtements au climat\n" +
                        "• Prévoyez un équipement pour les imprévus (parapluie, veste légère)\n" +
                        "• Vérifiez les saisons touristiques\n\n" +
                        "💡 **Astuce :** Les applications météo locales sont souvent plus précises !"
            }
            else -> {
                "Je comprends votre question ! 🤔\n\n" +
                        "Voici quelques pistes où je peux vous aider :\n\n" +
                        "🔹 **Créer un voyage** - Utilisez l'onglet Voyages\n" +
                        "🔹 **Gérer le budget** - Suivez vos dépenses en temps réel\n" +
                        "🔹 **Planifier des activités** - Organisez votre emploi du temps\n" +
                        "🔹 **Communiquer** - Échangez avec votre groupe\n" +
                        "🔹 **Notifications** - Restez informé des mises à jour\n\n" +
                        "Avez-vous une question spécifique sur l'une de ces fonctionnalités ?"
            }
        }
    }

    val quickReplies = listOf(
        "💰 Gestion de budget",
        "🗺️ Idées d'activités",
        "✈️ Planifier un voyage",
        "👥 Inviter des participants",
        "🍽️ Restaurants conseillés"
    )

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
            showQuickReplies = false
            isLoading = true

            scope.launch {
                delay(800) // Simulate AI thinking
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Turquoise40, Orange40)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 16.sp)
                        }
                        Column {
                            Text(
                                "Assistant IA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "TravelMate",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Chat messages area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF0F7FF),
                                Color(0xFFF8F9FA)
                            )
                        )
                    )
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = false
                ) {
                    // Welcome message
                    item {
                        if (messages.size == 1) {
                            WelcomeMessage()
                        }
                    }

                    // Messages
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            modifier = Modifier
                        )
                    }

                    // Loading indicator
                    if (isLoading) {
                        item {
                            BotTypingIndicator()
                        }
                    }
                }

                // Quick replies (floating at bottom)
                if (showQuickReplies && messages.size <= 3) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp)
                    ) {
                        QuickReplies(
                            replies = quickReplies,
                            onReplySelected = { reply ->
                                messageText = reply
                                sendMessage()
                            }
                        )
                    }
                }
            }

            // Input area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp)
                    .background(Color.White),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    // Suggestions chips
                    if (showQuickReplies && messages.size > 1) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            items(quickReplies.take(3)) { reply ->
                                SuggestionChip(
                                    onClick = {
                                        messageText = reply
                                        sendMessage()
                                    },
                                    label = { Text(reply, fontSize = 12.sp) },
                                    modifier = Modifier,
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Turquoise40.copy(alpha = 0.1f)
                                    )
                                )
                            }
                        }
                    }

                    // Input field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Posez votre question...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Turquoise40,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                focusedLabelColor = Turquoise40
                            ),
                            maxLines = 3,
                            enabled = !isLoading,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = Turquoise40.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )

                        IconButton(
                            onClick = { sendMessage() },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    color = if (messageText.isNotBlank() && !isLoading)
                                        Turquoise40 else Color.Gray.copy(alpha = 0.2f)
                                ),
                            enabled = !isLoading && messageText.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Envoyer",
                                tint = if (messageText.isNotBlank() && !isLoading)
                                    Color.White else Color.Gray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(-45f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Turquoise40.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Turquoise40, Orange40)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Je suis votre assistant TravelMate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Je peux vous aider à planifier vos voyages, gérer vos budgets, suggérer des activités et bien plus encore !",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Essayez de me demander :",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• Comment gérer mon budget ?\n• Quelles activités faire à Paris ?\n• Comment organiser mon voyage ?",
                fontSize = 12.sp,
                color = Turquoise40,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isBot = message.isFromBot

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
    ) {
        if (isBot) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Turquoise40.copy(alpha = 0.2f))
                    .border(1.dp, Turquoise40.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(if (isBot) 0.85f else 0.75f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isBot) 4.dp else 16.dp,
                bottomEnd = if (isBot) 16.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isBot) Color.White else Turquoise40
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    color = if (isBot) Color.Black else Color.White,
                    lineHeight = 20.sp
                )
            }
        }

        if (!isBot) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Orange40.copy(alpha = 0.2f))
                    .border(1.dp, Orange40.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun BotTypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Turquoise40)
                            .graphicsLayer {
                                translationY = (-8).dp.toPx() * sin(
                                    (System.currentTimeMillis() % 1000).toFloat() / 1000f * 2f * Math.PI.toFloat() +
                                            index * 0.5f
                                ).toFloat()
                            }
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Réflexion...",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun QuickReplies(
    replies: List<String>,
    onReplySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Sujets rapides",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                replies.forEach { reply ->
                    Surface(
                        onClick = { onReplySelected(reply) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Turquoise40.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, Turquoise40.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = reply,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            fontSize = 13.sp,
                            color = Turquoise40,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Turquoise40.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Turquoise40.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            label()
        }
    }
}