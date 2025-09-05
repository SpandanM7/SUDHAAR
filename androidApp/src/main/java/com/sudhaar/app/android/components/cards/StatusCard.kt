package com.sudhaar.app.android.components.cards

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sudhaar.app.android.R

// A richer, darker background for the overall screen
val RichDarkBackground = Color(0xFF0F0E17)
// A sophisticated primary color, used for the card background
val PrimaryDark = Color(0xFF232946)
// A soft, elegant accent color for titles and key text
val ElegantPink = Color(0xFF00CEC8)
// A more subtle, cool-toned accent for secondary text
val SubtleBlue = Color(0xFF6393A4)
// A deep shadow color for depth and dimension
val DeepShadow = Color(0xFF1B1B2A)

@Composable
fun StatusCard(
    images: List<String>?, // Nullable list of image CDN links
    title: String,
    status: String,
    modifier: Modifier = Modifier,
    // Add additional content to show when expanded
    expandedContent: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded } // Toggles the expanded state
            .animateContentSize(), // Animates size changes smoothly
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Retracted (closed) state layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Small image on the left
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val imageUrl = images?.firstOrNull()

                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Card image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(id = R.drawable.placeholder_image),
                            error = painterResource(id = R.drawable.placeholder_image),
                            fallback = painterResource(id = R.drawable.placeholder_image)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            contentDescription = "Placeholder",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title and Status text
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ElegantPink,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: $status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtleBlue
                    )
                }

                // Dropdown/Up arrow button
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = ElegantPink
                    )
                }
            }

            // Expanded content section, visible only when isExpanded is true
            if (isExpanded) {
                // Image section, now larger
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val imageUrl = images?.firstOrNull()
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Expanded image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            contentDescription = "Expanded Placeholder",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Additional content provided by the user
                Spacer(modifier = Modifier.height(16.dp))
                expandedContent()
            }
        }
    }
}