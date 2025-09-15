package com.sudhaar.app.android.screens.WelcomeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController) {
    FlowingGradientBackground {
        // Content over the flowing background
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Welcome content positioned higher up
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(200.dp))
                Text(
                    text = "Sudhaar",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Login/signup buttons in the vertical center
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                OutlinedButton(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color.White, Color.White))
                    ),
                    modifier = Modifier
                        .height(56.dp)
                        .width(140.dp)
                ) {
                    Text("Login", fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Signup click */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF667eea)
                    ),
                    modifier = Modifier
                        .height(56.dp)
                        .width(140.dp)
                ) {
                    Text("Sign Up", fontWeight = FontWeight.Bold)
                }
            }

            // Bottom tagline
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Report • Track • Transform",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 4.sp
                )
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FlowingGradientBackground(content: @Composable () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val center = Offset(widthPx / 2, heightPx / 2)

        val infiniteTransition = rememberInfiniteTransition()

        // Animate radius for breathing effect
        val radius by infiniteTransition.animateFloat(
            initialValue = minOf(widthPx, heightPx) / 3f,   // start smaller
            targetValue = maxOf(widthPx, heightPx),         // expand fully
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4B0000), // deep wine red
                            Color(0xFF800020), // wine/burgundy
                            Color.Black
                        ),
                        center = center,
                        radius = radius
                    )
                )
        ) {
            content()
        }
    }
}