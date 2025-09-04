package com.sudhaar.app.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sudhaar.app.android.utils.PlaceholderScreen
import com.sudhaar.app.android.components.cards.ClassyActionCard
import com.sudhaar.app.android.screens.LoggedInScreen.LoggedInScreen
import com.sudhaar.app.android.screens.WelcomeScreen.WelcomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudhaarTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun SudhaarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFF3F51B5),
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black,
        ),
        content = content
    )
}

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController)
        }
        composable("lodge_complaint") {
            PlaceholderScreen("Lodge a Complaint")
        }
        composable("view_complaints") {
            PlaceholderScreen("View Complaints")
        }
        composable("notifications") {
            PlaceholderScreen("No new notifications")
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val isLoggedIn = remember { isUserLoggedIn(context) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoggedIn) {
            LoggedInScreen(navController)
        } else {
            WelcomeScreen()
        }
    }
}


// Utility functions for checking login status
private fun isUserLoggedIn(context: Context): Boolean {
    // TODO: Remove hardcoding when login is implemented
    // For testing: Change this to false to test logged out state
    val isLoggedInForTesting = true

    if (isLoggedInForTesting) {
        return false
    }

    // Actual implementation (currently not used due to hardcoding above)
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("sudhaar_prefs", Context.MODE_PRIVATE)
    val jwtToken = sharedPreferences.getString("jwt_token", null)
    return !jwtToken.isNullOrEmpty()
}

private fun getUserName(context: Context): String {
    // TODO: Remove hardcoding when login is implemented
    // Hardcoded for testing
    return "Spandan"

    // Actual implementation (currently not used due to hardcoding above)
    // val sharedPreferences: SharedPreferences = context.getSharedPreferences("sudhaar_prefs", Context.MODE_PRIVATE)
    // return sharedPreferences.getString("user_name", "User") ?: "User"
}