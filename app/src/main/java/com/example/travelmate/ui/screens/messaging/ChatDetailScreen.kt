package com.example.travelmate.ui.screens.messaging

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.R
import com.example.travelmate.data.models.Message
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.MessageViewModel
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    travelId: String,
    travelTitle: String = "Chat de groupe",
    onNavigateBack: () -> Unit
) {
    val viewModel: MessageViewModel = travelMateViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val currentUserId = SessionManager.getCurrentUserId()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(travelId) {
        viewModel.loadMessages(travelId)
    }

    // Auto-scroll when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    // Show error snackbar if there's an error
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) {
            // In a real app, you'd use a SnackbarHost here
            println("Message Error: ${uiState.error}")
        }
    }

    Scaffold(
        topBar = {
            ChatDetailTopBar(
                travelTitle = travelTitle,
                participants = emptyList(),
                onBackClick = onNavigateBack,
                onMenuClick = { /* TODO: Open chat menu */ }
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(travelId, messageText)
                        messageText = ""
                    }
                },
                onAttachFile = { /* TODO: Attach file */ },
                onRecordAudio = { /* TODO: Record audio */ },
                isEnabled = !uiState.isSending
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF0F7FF), Color(0xFFF8F9FA))
                    )
                )
        ) {
            // Chat messages
            if (uiState.isLoading && uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Turquoise40,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else if (uiState.messages.isEmpty()) {
                EmptyChatState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    reverseLayout = false
                ) {
                    // Group messages by date
                    val groupedMessages = uiState.messages.groupBy {
                        val date = Date(it.timestamp)
                        when {
                            isToday(date) -> "Aujourd'hui"
                            isYesterday(date) -> "Hier"
                            else -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
                        }
                    }

                    groupedMessages.forEach { (dateLabel, dateMessages) ->
                        // Date header
                        stickyHeader {
                            DateLabel(dateLabel)
                        }

                        // Messages for this date
                        items(dateMessages) { message ->
                            MessageItem(
                                message = message,
                                isCurrentUser = message.senderId == currentUserId,
                                showSenderInfo = shouldShowSenderInfo(
                                    message,
                                    dateMessages,
                                    uiState.messages.indexOf(message)
                                )
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Scroll to bottom button
            if (uiState.messages.isNotEmpty() && listState.firstVisibleItemIndex < uiState.messages.size - 10) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 80.dp, end = 16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(uiState.messages.size - 1)
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = Turquoise40,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Aller en bas"
                        )
                    }
                }
            }

            // Error message display
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, Color(0xFFFF5252))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = uiState.error ?: "",
                                color = Color(0xFFFF5252),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Sending indicator
            if (uiState.isSending) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                ) {
                    CircularProgressIndicator(
                        color = Turquoise40,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailTopBar(
    travelTitle: String,
    participants: List<com.example.travelmate.data.models.User>,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Travel info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = travelTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Participants avatars
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            val maxAvatars = 3
                            participants.take(maxAvatars).forEachIndexed { index, participant ->
                                ParticipantAvatar(
                                    participant = participant,
                                    offset = index * 12
                                )
                            }
                            if (participants.size > maxAvatars) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .offset(x = (maxAvatars * 12).dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.3f))
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${participants.size - maxAvatars}",
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${participants.size} participants",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Menu button
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.Gray
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            scrolledContainerColor = Color.White
        )
    )
}

@Composable
fun ParticipantAvatar(
    participant: com.example.travelmate.data.models.User,
    offset: Int
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .offset(x = offset.dp)
            .clip(CircleShape)
            .background(Turquoise40.copy(alpha = 0.2f))
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = participant.name.take(2).uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Turquoise40
        )
    }
}

@Composable
fun DateLabel(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Turquoise40.copy(alpha = 0.1f),
            contentColor = Turquoise40
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isCurrentUser: Boolean,
    showSenderInfo: Boolean
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isCurrentUser && showSenderInfo) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Turquoise40.copy(alpha = 0.2f))
                        .border(1.dp, Turquoise40.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.senderId.take(1).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Turquoise40
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
            ) {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentUser) Turquoise40 else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = message.content,
                            fontSize = 14.sp,
                            color = if (isCurrentUser) Color.White else Color.Black,
                            lineHeight = 20.sp
                        )

                        // Message status and time
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isCurrentUser) {
                                // Message status (sent, delivered, read)
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = timeFormat.format(Date(message.timestamp)),
                                fontSize = 10.sp,
                                color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onAttachFile: () -> Unit,
    onRecordAudio: () -> Unit,
    isEnabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            // Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach button
                IconButton(
                    onClick = onAttachFile,
                    modifier = Modifier.size(40.dp),
                    enabled = isEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Joindre un fichier",
                        tint = if (isEnabled) Turquoise40 else Turquoise40.copy(alpha = 0.5f)
                    )
                }

                // Voice message button
                IconButton(
                    onClick = onRecordAudio,
                    modifier = Modifier.size(40.dp),
                    enabled = isEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Message vocal",
                        tint = if (isEnabled) Turquoise40 else Turquoise40.copy(alpha = 0.5f)
                    )
                }

                // Input field
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Écrire un message...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Turquoise40,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        focusedLabelColor = Turquoise40,
                        disabledTextColor = Color.Gray.copy(alpha = 0.5f),
                        disabledBorderColor = Color.Gray.copy(alpha = 0.2f)
                    ),
                    maxLines = 4,
                    enabled = isEnabled,
                    trailingIcon = {
                        if (messageText.isNotBlank() && isEnabled) {
                            IconButton(
                                onClick = onSendMessage,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Envoyer",
                                    tint = Turquoise40,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .rotate(-45f)
                                )
                            }
                        }
                    }
                )

                // Emoji button
                IconButton(
                    onClick = { /* TODO: Show emoji picker */ },
                    modifier = Modifier.size(40.dp),
                    enabled = isEnabled
                ) {
                    Text(
                        text = "😊",
                        fontSize = 20.sp
                    )
                }
            }

            // Quick reply suggestions
            if (messageText.isBlank()) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    QuickRepliesRow(
                        replies = listOf("Ça va ?", "On se retrouve où ?", "À quelle heure ?", "Budget ?"),
                        onReplySelected = onMessageTextChange
                    )
                }
            }
        }
    }
}

@Composable
fun QuickRepliesRow(
    replies: List<String>,
    onReplySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        replies.forEach { reply ->
            SuggestionChip(
                onClick = { onReplySelected(reply) },
                label = { Text(reply, fontSize = 12.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Turquoise40.copy(alpha = 0.1f),
                    labelColor = Turquoise40
                )
            )
        }
    }
}

@Composable
fun EmptyChatState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Turquoise40.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                tint = Turquoise40,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Aucun message",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Soyez le premier à envoyer un message !\nDiscutez avec votre groupe pour planifier votre voyage.",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* TODO: Start conversation */ },
            modifier = Modifier
                .height(40.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Turquoise40.copy(alpha = 0.1f),
                contentColor = Turquoise40
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HistoryEdu,
                    contentDescription = "Historique",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voir l'historique des activités")
            }
        }
    }
}

fun shouldShowSenderInfo(
    currentMessage: Message,
    dateMessages: List<Message>,
    index: Int
): Boolean {
    if (index == 0) return true
    val previousMessage = dateMessages.getOrNull(index - 1)
    return previousMessage?.senderId != currentMessage.senderId
}

fun isToday(date: Date): Boolean {
    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)
}

fun isYesterday(date: Date): Boolean {
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
    val target = Calendar.getInstance().apply { time = date }
    return yesterday.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            yesterday.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
            yesterday.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)
}