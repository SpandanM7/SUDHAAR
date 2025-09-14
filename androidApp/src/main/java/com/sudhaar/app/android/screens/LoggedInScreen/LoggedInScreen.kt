package com.sudhaar.app.android.screens.LoggedInScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sudhaar.app.android.components.cards.ClassyActionCard
import com.sudhaar.app.android.utils.AuthManager
import kotlinx.coroutines.launch

@Composable
fun LoggedInScreen(navController: NavController, authManager: AuthManager) {
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf(authManager.getUserName()) }
    var isLoadingName by remember { mutableStateOf(false) }

    // Fetch user name from API on screen load
    LaunchedEffect(Unit) {
        isLoadingName = true
        try {
            val fetchedName = authManager.fetchUserName()
            userName = fetchedName
        } catch (e: Exception) {
            // Keep the cached name if API call fails
        } finally {
            isLoadingName = false
        }
    }

    // Whole screen background → deep black
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with notification and logout buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logout button
                IconButton(
                    onClick = {
                        scope.launch {
                            val result = authManager.logout()
                            // Navigate back to welcome screen regardless of API result
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFFF6B6B)
                    )
                }

                // Notification button
                IconButton(
                    onClick = { navController.navigate("notifications") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF79DB70)
                    )
                }
            }

            // Welcome section (flat dark card)
            // Welcome section (flat dark card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF005451)) // very dark wine
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(), // This is crucial for alignment
                    horizontalAlignment = Alignment.Start // Left-aligns the content of the Column
                ) {
                    Text(
                        text = "Welcome back,",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Start // Ensures the text itself is left-aligned
                    )

                    if (isLoadingName) {
                        Box(
                            modifier = Modifier.padding(top = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF00CEC8)
                            )
                        }
                    } else {
                        Text(
                            text = userName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00CEC8),
                            textAlign = TextAlign.Start, // Ensures the text itself is left-aligned
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Main action cards (dark bg + wine accent)
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ClassyActionCard(
                    title = "Lodge a Complaint",
                    subtitle = "Report civic issues in your area",
                    icon = Icons.Default.Add,
                    onClick = { navController.navigate("complaint_details") },
                    titleColor = Color.White,
                    subtitleColor = Color.White.copy(alpha = 0.7f),
                    iconTint = Color(0xFF00CEC8),
                    backgroundColor = Color(0xFF111111) // flat dark card
                )

                ClassyActionCard(
                    title = "View Complaints",
                    subtitle = "Track your reported issues",
                    icon = Icons.Default.List,
                    onClick = { navController.navigate("view_complaints") },
                    titleColor = Color.White,
                    subtitleColor = Color.White.copy(alpha = 0.7f),
                    iconTint = Color(0xFF00CEC8),
                    backgroundColor = Color(0xFF111111)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}