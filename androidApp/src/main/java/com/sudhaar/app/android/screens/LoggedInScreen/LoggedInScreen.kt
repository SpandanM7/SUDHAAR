package com.sudhaar.app.android.screens.LoggedInScreen

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sudhaar.app.android.components.cards.ClassyActionCard

@Composable
fun LoggedInScreen(navController: NavController) {
    val context = LocalContext.current
    val userName = remember { getUserName(context) }

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
            // Top bar with notification button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF005451)) // very dark wine
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome back,",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = userName,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00CEC8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
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
                    onClick = { navController.navigate("lodge_complaint") },
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


private fun getUserName(context: Context): String {
    // TODO: Remove hardcoding when login is implemented
    // Hardcoded for testing
    return "Spandan"

    // Actual implementation (currently not used due to hardcoding above)
    // val sharedPreferences: SharedPreferences = context.getSharedPreferences("sudhaar_prefs", Context.MODE_PRIVATE)
    // return sharedPreferences.getString("user_name", "User") ?: "User"
}