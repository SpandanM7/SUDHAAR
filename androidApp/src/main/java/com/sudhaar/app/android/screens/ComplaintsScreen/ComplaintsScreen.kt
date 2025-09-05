package com.sudhaar.app.android.screens.ComplaintsScreen

import com.sudhaar.app.android.components.cards.StatusCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sudhaar.app.android.components.cards.SubtleBlue


// Dark background recommendation for the screen
val DarkBackground = androidx.compose.ui.graphics.Color(0xFF121212)
val AccentGreen = androidx.compose.ui.graphics.Color(0xFF79DB70)
val AccentTeal = androidx.compose.ui.graphics.Color(0xFF00CEC8)
val AccentDark = androidx.compose.ui.graphics.Color(0xFF005451)

@Composable
fun ComplaintsScreen(
    navController: NavController
) {
    val complaints = GetComplaints() // fetch complaints (hardcoded for now)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Top heading
            Text(
                text = "YOUR COMPLAINTS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AccentTeal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Complaints list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
        )
    )
}