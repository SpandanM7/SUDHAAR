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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.net.Uri
import coil.compose.AsyncImage

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
        composable("lodge_complaint") {
            ComplaintTypesScreen(
                onComplaintTypeSelected = { complaintType ->
                    navController.navigate("complaint_details/$complaintType")
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "complaint_details/{complaintType}",
            arguments = listOf(navArgument("complaintType") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintType = backStackEntry.arguments?.getString("complaintType") ?: ""
            ComplaintDetailsScreen(
                complaintType = complaintType,
                onBackPressed = {
                    navController.popBackStack()
                },
                onSubmit = { imageUri, description ->
                    // Handle submission logic here
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


// Complaint Types Screen
@Composable
fun ComplaintTypesScreen(
    onComplaintTypeSelected: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    val complaintTypes = listOf(
        "Road Condition",
        "Garbage Mismanagement",
        "Water Supply Issues",
        "Electricity Problems",
        "Drainage Issues",
        "Street Lighting",
        "Public Transport",
        "Noise Pollution",
        "Illegal Construction",
        "Other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Select Complaint Type",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Complaint Types List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(complaintTypes) { complaintType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onComplaintTypeSelected(complaintType) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (complaintType) {
                                "Road Condition" -> Icons.Default.Build
                                "Garbage Mismanagement" -> Icons.Default.Delete
                                "Water Supply Issues" -> Icons.Default.WaterDrop
                                "Electricity Problems" -> Icons.Default.ElectricBolt
                                "Drainage Issues" -> Icons.Default.WaterDrop
                                "Street Lighting" -> Icons.Default.Lightbulb
                                "Public Transport" -> Icons.Default.DirectionsBus
                                "Noise Pollution" -> Icons.Default.VolumeOff
                                "Illegal Construction" -> Icons.Default.Construction
                                else -> Icons.Default.ReportProblem
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = complaintType,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

// Complaint Details Screen
@Composable
fun ComplaintDetailsScreen(
    complaintType: String,
    onBackPressed: () -> Unit,
    onSubmit: (Uri?, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        imageError = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Lodge Complaint",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = complaintType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Image Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Add Image *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            TextButton(
                                onClick = { launcher.launch("image/*") },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Change Image")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { launcher.launch("image/*") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                border = BorderStroke(
                                    2.dp,
                                    if (imageError != null) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Tap to add image",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        if (imageError != null) {
                            Text(
                                text = imageError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Description Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Describe Your Issue *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { newValue ->
                                if (newValue.length <= 500) {
                                    description = newValue
                                    descriptionError = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Please provide detailed description of the issue...") },
                            minLines = 4,
                            maxLines = 8,
                            isError = descriptionError != null,
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = descriptionError ?: "",
                                        color = if (descriptionError != null) MaterialTheme.colorScheme.error
                                        else Color.Transparent
                                    )
                                    Text(
                                        text = "${description.length}/500",
                                        color = if (description.length > 450) MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        var hasErrors = false

                        // Validate image
                        if (selectedImageUri == null) {
                            imageError = "Please add an image"
                            hasErrors = true
                        }

                        // Validate description
                        if (description.trim().isEmpty()) {
                            descriptionError = "Please describe the issue"
                            hasErrors = true
                        } else if (description.trim().length < 10) {
                            descriptionError = "Please provide more details (minimum 10 characters)"
                            hasErrors = true
                        }

                        if (!hasErrors) {
                            onSubmit(selectedImageUri, description.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = true
                ) {
                    Text(
                        text = "Submit Complaint",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
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