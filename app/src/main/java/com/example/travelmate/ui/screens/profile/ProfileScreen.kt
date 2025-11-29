package com.example.travelmate.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelmate.ui.composables.travelMateViewModel
import com.example.travelmate.ui.viewmodel.AuthViewModel
import com.example.travelmate.ui.viewmodel.ProfileViewModel
import com.example.travelmate.ui.theme.Orange40
import com.example.travelmate.ui.theme.Turquoise40
import com.example.travelmate.util.SessionManager

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val profileViewModel: ProfileViewModel = travelMateViewModel()
    val authViewModel: AuthViewModel = travelMateViewModel()
    
    val user by profileViewModel.user.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        profileViewModel.loadUser()
    }
    
    val currentUserName = SessionManager.getCurrentUserName() ?: "Utilisateur"
    val currentUserEmail = SessionManager.getCurrentUserEmail() ?: ""
    val userRole = user?.role?.name ?: "Participant"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Turquoise40,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = Turquoise40)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Turquoise40.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = Turquoise40
                        ) {
                            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                Text(
                                    text = currentUserName.take(2).uppercase(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = currentUserName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = currentUserEmail,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = Turquoise40.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = userRole,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Turquoise40
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ListItem(
                    headlineContent = { Text("Paramètres") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Turquoise40
                        )
                    }
                )
                
                Divider()
                
                ListItem(
                    headlineContent = { Text("Aide et support") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = null,
                            tint = Turquoise40
                        )
                    }
                )
                
                Divider()
                
                ListItem(
                    headlineContent = { Text("À propos") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Turquoise40
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange40
                )
            ) {
                Text("Se déconnecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
