package com.sudhaar.app.android.screens.ComplaintsScreen

//import com.sudhaar.app.android.components.cards.StatusCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sudhaar.app.android.components.cards.StatusCard
import com.sudhaar.app.android.components.cards.SubtleBlue


// Dark background recommendation for the screen
val DarkBackground = Color(0xFF121212)
val AccentGreen = Color(0xFF79DB70)
val AccentTeal = Color(0xFF00CEC8)
val AccentDark = Color(0xFF005451)
val SubtleBlue = Color(0xFF6393A4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen(
    navController: NavController
) {
    val complaints = GetComplaints() // fetch complaints (hardcoded for now)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "YOUR COMPLAINTS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentTeal
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AccentTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        // Complaints list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(complaints) { complaint ->
                StatusCard(
                    images = complaint.images,
                    title = complaint.title,
                    status = complaint.status
                ) {
                    // This is the expanded content section
                    // Add any additional UI elements you want to show when the card is open
                    Text(
                        text = "Details: ${complaint.description}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SubtleBlue,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // You can add more text, buttons, or a detailed view here
                }
            }
        }
    }
}

    // A simple model to hold complaint data
    data class ComplaintItem(
        val images: List<String>?,
        val title: String,
        val status: String,
        val description: String
    )

    // Fake data provider (later you can replace with repository/api call)
    fun GetComplaints(): List<ComplaintItem> {
        return listOf(
            ComplaintItem(
                images = null,//listOf("https://cdn.example.com/road.jpg"),
                title = "Broken Road",
                status = "Pending",
                description = "A large pothole has formed on the main road, making it dangerous for vehicles and pedestrians. It's located near the city park entrance."
            ),
            ComplaintItem(
                images = null,
                title = "Garbage Collection",
                status = "In Progress",
                description = "Garbage has not been collected from Sector 4 for the past three days. The bins are overflowing and causing a foul odor."
            ),
            ComplaintItem(
                images = null,//listOf("https://cdn.example.com/streetlight.jpg"),
                title = "Streetlight Not Working",
                status = "Completed",
                description = "The streetlight at the intersection of First and Main Street has been fixed. The area is now well-lit, improving night-time safety."
            ),
            ComplaintItem(
                images = null,//listOf("https://cdn.example.com/streetlight.jpg"),
                title = "Streetlight Not Working",
                status = "Failed",
                description = "The streetlight at the intersection of First and Main Street has been fixed. The area is now well-lit, improving night-time safety."
            )
        )
    }