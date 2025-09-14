package com.sudhaar.app.android.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class LoginRequest(
    val mobile: String,
    val password: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class RefreshResponse(
    val accessToken: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NameResponse(
    val name: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ErrorResponse(
    val error: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
data class AuthResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class AuthManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sudhaar_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    private companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val USER_NAME_KEY = "user_name"
        const val BASE_URL = "https://sudhaar-backend-mob.onrender.com"
        const val TAG = "AuthManager"
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
        return !accessToken.isNullOrEmpty()
    }

    // Get stored user name
    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME_KEY, "User") ?: "User"
    }

    // Login function
    suspend fun login(mobile: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/login")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val loginRequest = LoginRequest(mobile, password)
                val requestBody = json.encodeToString(loginRequest)

                connection.outputStream.use { outputStream ->
                    outputStream.write(requestBody.toByteArray())
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val loginResponse = json.decodeFromString<LoginResponse>(responseBody)

                    // Store tokens
                    with(sharedPreferences.edit()) {
                        putString(ACCESS_TOKEN_KEY, loginResponse.accessToken)
                        putString(REFRESH_TOKEN_KEY, loginResponse.refreshToken)
                        apply()
                    }

                    Log.d(TAG, "Login successful")
                    AuthResult(isSuccess = true)
                } else {
                    val errorResponse = try {
                        json.decodeFromString<ErrorResponse>(responseBody)
                    } catch (e: Exception) {
                        ErrorResponse("Login failed")
                    }

                    Log.e(TAG, "Login failed: ${errorResponse.error}")
                    AuthResult(isSuccess = false, errorMessage = errorResponse.error)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                AuthResult(isSuccess = false, errorMessage = "Network error: ${e.message}")
            }
        }
    }

    // Fetch user name from API
    suspend fun fetchUserName(): String {
        return withContext(Dispatchers.IO) {
            try {
                val result = makeAuthenticatedRequest("$BASE_URL/name", "GET")

                if (result.isSuccess && result.responseBody != null) {
                    val nameResponse = json.decodeFromString<NameResponse>(result.responseBody)

                    // Store the name locally
                    with(sharedPreferences.edit()) {
                        putString(USER_NAME_KEY, nameResponse.name)
                        apply()
                    }

                    Log.d(TAG, "User name fetched: ${nameResponse.name}")
                    nameResponse.name
                } else {
                    Log.e(TAG, "Failed to fetch user name: ${result.errorMessage}")
                    getUserName() // Return cached name
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user name", e)
                getUserName() // Return cached name
            }
        }
    }

    // Generic function to make authenticated requests with automatic token refresh
    suspend fun makeAuthenticatedRequest(
        url: String,
        method: String = "GET",
        body: String? = null
    ): ApiResult {
        return withContext(Dispatchers.IO) {
            var accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

            if (accessToken.isNullOrEmpty()) {
                return@withContext ApiResult(
                    isSuccess = false,
                    errorMessage = "No access token available"
                )
            }

            // Try the request with current access token
            var result = performRequest(url, method, body, accessToken)

            // If we get 401, try to refresh the token and retry
            if (result.responseCode == 401) {
                Log.d(TAG, "Access token expired, attempting refresh")

                val refreshResult = refreshAccessToken()
                if (refreshResult.isSuccess) {
                    // Get the new access token and retry the request
                    accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
                    if (!accessToken.isNullOrEmpty()) {
                        result = performRequest(url, method, body, accessToken)
                    }
                } else {
                    // Refresh failed, user needs to login again
                    logout()
                    return@withContext ApiResult(
                        isSuccess = false,
                        errorMessage = "Session expired. Please login again."
                    )
                }
            }

            result
        }
    }

    // Perform HTTP request with bearer token
    private suspend fun performRequest(
        url: String,
        method: String,
        body: String?,
        accessToken: String
    ): ApiResult {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = method
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Content-Type", "application/json")

            if (body != null && (method == "POST" || method == "PUT")) {
                connection.doOutput = true
                connection.outputStream.use { outputStream ->
                    outputStream.write(body.toByteArray())
                }
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            ApiResult(
                isSuccess = responseCode in 200..299,
                responseCode = responseCode,
                responseBody = responseBody,
                errorMessage = if (responseCode !in 200..299) {
                    try {
                        val errorResponse = json.decodeFromString<ErrorResponse>(responseBody)
                        errorResponse.error
                    } catch (e: Exception) {
                        "Request failed with code: $responseCode"
                    }
                } else null
            )

        } catch (e: Exception) {
            Log.e(TAG, "Request error", e)
            ApiResult(
                isSuccess = false,
                errorMessage = "Network error: ${e.message}"
            )
        }
    }

    // Refresh access token using refresh token
    private suspend fun refreshAccessToken(): AuthResult {
        return try {
            val refreshToken = sharedPreferences.getString(REFRESH_TOKEN_KEY, null)

            if (refreshToken.isNullOrEmpty()) {
                return AuthResult(isSuccess = false, errorMessage = "No refresh token available")
            }

            val url = URL("$BASE_URL/refresh")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val refreshRequest = RefreshRequest(refreshToken)
            val requestBody = json.encodeToString(refreshRequest)

            connection.outputStream.use { outputStream ->
                outputStream.write(requestBody.toByteArray())
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val refreshResponse = json.decodeFromString<RefreshResponse>(responseBody)

                // Store new access token
                with(sharedPreferences.edit()) {
                    putString(ACCESS_TOKEN_KEY, refreshResponse.accessToken)
                    apply()
                }

                Log.d(TAG, "Token refreshed successfully")
                AuthResult(isSuccess = true)
            } else {
                Log.e(TAG, "Token refresh failed: $responseBody")
                AuthResult(isSuccess = false, errorMessage = "Token refresh failed")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Token refresh error", e)
            AuthResult(isSuccess = false, errorMessage = "Network error: ${e.message}")
        }
    }

    // Logout - call API and clear all stored data
    suspend fun logout(): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val refreshToken = sharedPreferences.getString(REFRESH_TOKEN_KEY, null)

                if (!refreshToken.isNullOrEmpty()) {
                    // Call logout API
                    val url = URL("$BASE_URL/logout")
                    val connection = url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    val logoutRequest = RefreshRequest(refreshToken)
                    val requestBody = json.encodeToString(logoutRequest)

                    connection.outputStream.use { outputStream ->
                        outputStream.write(requestBody.toByteArray())
                    }

                    val responseCode = connection.responseCode
                    val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                    }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "Logout API call successful")
                    } else {
                        Log.w(TAG, "Logout API failed: $responseBody")
                        // Continue with local logout even if API fails
                    }
                }

                // Always clear local data regardless of API response
                clearLocalData()
                AuthResult(isSuccess = true)

            } catch (e: Exception) {
                Log.e(TAG, "Logout error", e)
                // Clear local data even if API call fails
                clearLocalData()
                AuthResult(isSuccess = true) // Always return success for logout
            }
        }
    }

    // Clear local stored data
    private fun clearLocalData() {
        with(sharedPreferences.edit()) {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            remove(USER_NAME_KEY)
            apply()
        }
        Log.d(TAG, "Local user data cleared")
    }
}

data class ApiResult(
    val isSuccess: Boolean,
    val responseCode: Int = 0,
    val responseBody: String? = null,
    val errorMessage: String? = null
)