package com.sudhaar.app.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sudhaar.app.android.utils.PlaceholderScreen
import com.sudhaar.app.android.components.cards.ClassyActionCard
import com.sudhaar.app.android.screens.ComplaintsScreen.ComplaintsScreen
import com.sudhaar.app.android.screens.LoggedInScreen.LoggedInScreen
import com.sudhaar.app.android.screens.WelcomeScreen.WelcomeScreen
import android.util.Log
import com.sudhaar.app.android.screens.ComplaintDetailsScreen.ComplaintDetailsScreen

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
        startDestination = "main",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        }

    ) {
        composable("main") {
            MainScreen(navController)
        }

        composable(
            route = "complaint_details"
        ) { backStackEntry ->
            val complaintType = ""  // Or set a default value like "General Complaint"
            ComplaintDetailsScreen(
                complaintType = complaintType,
                onBackPressed = {
                    navController.popBackStack()
                },
                onSubmit = { imageUri, description, selectedCategory ->
                    // Handle submission logic here
                    // You now have access to:
                    // - imageUri: Uri? - The selected image
                    // - description: String - The complaint description
                    // - selectedCategory: String - The selected complaint category

                    // Example: Log the data or send to your backend
                    Log.d("ComplaintSubmission", "Category: $selectedCategory")
                    Log.d("ComplaintSubmission", "Description: $description")
                    Log.d("ComplaintSubmission", "Image URI: $imageUri")

                    // For now, just show success or navigate back
                    navController.popBackStack("lodge_complaint", inclusive = true)
                }
            )
        }
        composable("view_complaints") {
            ComplaintsScreen(navController)
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
        return true
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