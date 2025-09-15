package com.sudhaar.app.android.screens.LoginScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sudhaar.app.android.utils.AuthManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val scope = rememberCoroutineScope()

    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF800020))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Back",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign in to continue",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(24.dp)
            ) {
                Column {
                    // Mobile number field with max length validation
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { newValue ->
                            if (newValue.length <= 10) {
                                mobile = newValue
                            }
                            errorMessage = ""
                        },
                        label = { Text("Mobile Number", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Enter mobile number", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = "Phone Icon", tint = Color(0xFF00CEC8))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00CEC8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color(0xFF00CEC8),
                            focusedLabelColor = Color(0xFF00CEC8),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field with max length validation
                    OutlinedTextField(
                        value = password,
                        onValueChange = { newValue ->
                            if (newValue.length <= 16) {
                                password = newValue
                            }
                            errorMessage = ""
                        },
                        label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Enter password", color = Color.White.copy(alpha = 0.5f)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Icon", tint = Color(0xFF00CEC8))
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00CEC8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color(0xFF00CEC8),
                            focusedLabelColor = Color(0xFF00CEC8),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFFF6B6B),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (mobile.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }

                            scope.launch {
                                isLoading = true
                                errorMessage = ""

                                val result = authManager.login(mobile, password)

                                isLoading = false

                                if (result.isSuccess) {
                                    navController.navigate("main") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = result.errorMessage ?: "Login failed"
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00CEC8),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF00CEC8).copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "LOGIN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}