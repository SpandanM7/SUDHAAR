package com.sudhaar.app.android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen {
                // Navigate to main activity when animation completes
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
                // Add slide transition
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }
}

@Composable
fun SplashScreen(onAnimationComplete: () -> Unit) {
    // Animation states
    var animationPhase by remember { mutableStateOf(0) }
    var textAnimationPhase by remember { mutableStateOf(0) }
    var visibleLetterCount by remember { mutableStateOf(0) }

    val appName = "SUDHAAR"

    // Scale animation with different phases
    val scaleAnimation = animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.3f    // Start small
            1 -> 1.3f    // Grow big
            2 -> 1.0f    // Settle to normal size
            else -> 1.0f
        },
        animationSpec = when (animationPhase) {
            0 -> tween(durationMillis = 100) // Quick start
            1 -> tween(durationMillis = 800, easing = FastOutSlowInEasing) // Smooth grow
            2 -> tween(durationMillis = 400, easing = FastOutSlowInEasing) // Gentle settle
            else -> tween(durationMillis = 400)
        },
        finishedListener = {
            when (animationPhase) {
                1 -> animationPhase = 2 // Move to settle phase after growing
                2 -> {
                    // Logo animation complete, start text animation
                    Handler(Looper.getMainLooper()).postDelayed({
                        textAnimationPhase = 1 // Start text animation
                    }, 300) // Short delay before text starts
                }
            }
        }
    )

    // Text animation - reveals letters one by one
    LaunchedEffect(textAnimationPhase) {
        if (textAnimationPhase == 1) {
            for (i in 0..appName.length) {
                delay(150) // 150ms between each letter
                visibleLetterCount = i
            }
            // After text animation completes, wait then navigate
            delay(1000) // Hold complete text for 1 second
            onAnimationComplete()
        }
    }

    // Alpha animation for smooth fade in
    val alphaAnimation = animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    // Text alpha animation
    val textAlphaAnimation = animateFloatAsState(
        targetValue = if (textAnimationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    // Start the animation sequence
    LaunchedEffect(Unit) {
        delay(200) // Small delay before starting
        animationPhase = 1 // Start the grow animation
    }

    // Keep the logo visible throughout - no separate slide animation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1D1E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.mipmap.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scaleAnimation.value)
                    .alpha(alphaAnimation.value)
            )

            // App Name with typewriter effect
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = appName.take(visibleLetterCount),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5DADA6), // Teal color matching your logo
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .alpha(textAlphaAnimation.value)
            )
        }
    }
}