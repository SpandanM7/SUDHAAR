package com.sudhaar.app.android.components.cards

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sudhaar.app.android.R

// Enhanced color palette with gradients and depth
val RichDarkBackground = Color(0xFF0F0E17)
val PrimaryDarkCard = Color(0xFF1C1C27)
val SecondaryCard = Color(0xFF232332)
val AccentTeal = Color(0xFF00CEC8)
val AccentTealLight = Color(0xFF4DDAD6)
val TextPrimary = Color(0xFFE8E8E8)
val TextSecondary = Color(0xFFA8A8B3)
val SubtleBlue = Color(0xFF6393A4)
val GlowColor = Color(0x1A00CEC8)

// Status color mapping
fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "pending", "completed", "success" -> Color(0xFF4CAF50)
        "active", "in progress", "processing" -> Color(0xFFFF9800)
        "failed", "error", "cancelled" -> Color(0xFFF44336)
        "paused", "waiting" -> Color(0xFF2196F3)
        else -> AccentTeal
    }
}

@Composable
fun StatusCard(
    images: List<String>?,
    title: String,
    status: String,
    modifier: Modifier = Modifier,
    expandedContent: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val statusColor = getStatusColor(status)

    // Animation values
    val animatedProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "expansion"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isExpanded) 12.dp else 4.dp,
        animationSpec = tween(300),
        label = "shadow"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isExpanded = !isExpanded
            }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .drawWithCache {
                onDrawBehind {
                    // Subtle glow effect
                    if (isExpanded) {
                        drawRoundRect(
                            color = GlowColor,
                            size = size,
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryDarkCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = shadowElevation)
    ) {
        // Gradient overlay for depth
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.03f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                // Header section with enhanced layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Enhanced image with glow and better styling
                    Box(
                        modifier = Modifier.size(64.dp)
                    ) {
                        // Glow background for image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            statusColor.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Main image container
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SecondaryCard)
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
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
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Enhanced title and status section
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Status chip with enhanced styling
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status indicator dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = statusColor,
                                        shape = CircleShape
                                    )
                                    .drawWithCache {
                                        onDrawBehind {
                                            // Subtle glow around status dot
                                            drawCircle(
                                                color = statusColor.copy(alpha = 0.3f),
                                                radius = size.minDimension / 1.5f
                                            )
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = statusColor
                            )
                        }
                    }

                    // Enhanced expand/collapse button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AccentTeal.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { isExpanded = !isExpanded },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = AccentTeal,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer {
                                    rotationZ = animatedProgress * 180f
                                }
                        )
                    }
                }

                // Enhanced expanded content with smooth animations
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400)),
                    exit = fadeOut(animationSpec = tween(300)) +
                            shrinkVertically(animationSpec = tween(300))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced image section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SecondaryCard)
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(16.dp)
                                )
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

                            // Subtle gradient overlay for better text readability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            ),
                                            startY = 0.6f
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced content section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.02f),
                                            Color.White.copy(alpha = 0.01f)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            expandedContent()
                        }
                    }
                }
            }
        }
    }
}