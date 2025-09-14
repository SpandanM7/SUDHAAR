package com.sudhaar.app.android.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * ApiService provides a convenient way to make authenticated API calls
 * with automatic token refresh handling.
 */
class ApiService(private val context: Context) {
    private val authManager = AuthManager(context)
    private val json = Json { ignoreUnknownKeys = true }

    private companion object {
        const val TAG = "ApiService"
    }

    /**
     * Make a GET request with authentication
     */
    suspend fun get(url: String): ApiResult {
        return authManager.makeAuthenticatedRequest(url, "GET")
    }

    /**
     * Make a POST request with authentication
     */
    suspend fun post(url: String, body: String): ApiResult {
        return authManager.makeAuthenticatedRequest(url, "POST", body)
    }

    /**
     * Make a PUT request with authentication
     */
    suspend fun put(url: String, body: String): ApiResult {
        return authManager.makeAuthenticatedRequest(url, "PUT", body)
    }

    /**
     * Make a DELETE request with authentication
     */
    suspend fun delete(url: String): ApiResult {
        return authManager.makeAuthenticatedRequest(url, "DELETE")
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }

    /**
     * Get current user name
     */
    fun getCurrentUserName(): String {
        return authManager.getUserName()
    }

    /**
     * Logout user
     */
//    fun logout() {
//        return authManager.logout()
//    }

    /**
     * Example usage for other screens:
     *
     * // In your composable or ViewModel
     * val apiService = ApiService(context)
     *
     * // Making a request
     * scope.launch {
     *     val result = apiService.get("https://sudhaar-backend-mob.onrender.com/complaints")
     *     if (result.isSuccess) {
     *         // Handle successful response
     *         val responseData = result.responseBody
     *     } else {
     *         // Handle error
     *         val errorMessage = result.errorMessage
     *     }
     * }
     */
}

/**
 * Extension functions for common API response handling
 */

/**
 * Parse JSON response to a specific type
 */
inline fun <reified T> ApiResult.parseJson(): T? {
    return if (isSuccess && responseBody != null) {
        try {
            Json.decodeFromString<T>(responseBody)
        } catch (e: Exception) {
            Log.e("ApiService", "Failed to parse JSON response", e)
            null
        }
    } else {
        null
    }
}

/**
 * Execute a block of code if the API call was successful
 */
inline fun ApiResult.onSuccess(block: (String) -> Unit): ApiResult {
    if (isSuccess && responseBody != null) {
        block(responseBody)
    }
    return this
}

/**
 * Execute a block of code if the API call failed
 */
inline fun ApiResult.onError(block: (String?) -> Unit): ApiResult {
    if (!isSuccess) {
        block(errorMessage)
    }
    return this
}

/**
 * Example usage in a screen:
 *
 * @Composable
 * fun MyScreen() {
 *     val context = LocalContext.current
 *     val apiService = remember { ApiService(context) }
 *     val scope = rememberCoroutineScope()
 *
 *     LaunchedEffect(Unit) {
 *         apiService.get("https://sudhaar-backend-mob.onrender.com/data")
 *             .onSuccess { response ->
 *                 // Handle success
 *                 val data = Json.decodeFromString<MyDataClass>(response)
 *             }
 *             .onError { error ->
 *                 // Handle error
 *                 Log.e("MyScreen", "API error: $error")
 *             }
 *     }
 * }
 */