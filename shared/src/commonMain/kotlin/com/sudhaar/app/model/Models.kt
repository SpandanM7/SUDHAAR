package com.sudhaar.app.model

// ---------- Request Bodies ----------

// Login request body
data class LoginRequest(
    val mobile: String,
    val password: String
)

// Refresh token request body
data class RefreshRequest(
    val refreshToken: String
)

// Logout request body
data class LogoutRequest(
    val refreshToken: String
)


// ---------- Responses ----------

// Generic error response (in case of failure)
data class ErrorResponse(
    val error: String
)

// Login success response
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

// Protected /name response
data class NameResponse(
    val name: String
)

// Refresh success response
data class RefreshResponse(
    val accessToken: String
)

// Logout success response
data class LogoutResponse(
    val ok: Boolean,
    val message: String
)

// Health check response (/test-db)
data class HealthResponse(
    val ok: Boolean,
    val error: String? = null
)

// Base endpoint response (/)
data class BaseResponse(
    val status: String,
    val timestamp: String
)
