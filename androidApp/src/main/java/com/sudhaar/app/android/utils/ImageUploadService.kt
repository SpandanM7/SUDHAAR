package com.sudhaar.app.android.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ImgBBUploadResponse(
    val data: ImgBBData? = null,
    val success: Boolean = false,
    val error: ImgBBError? = null
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ImgBBData(
    val url: String,
    val display_url: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ImgBBError(
    val message: String
)

data class ImageUploadResult(
    val isSuccess: Boolean,
    val imageUrl: String? = null,
    val errorMessage: String? = null
)

class ImageUploadService(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val TAG = "ImageUploadService"
        // Free ImgBB API key - You should get your own from https://api.imgbb.com/
        private const val IMGBB_API_KEY = "0bca35f3ddad73f5807f7b319b01c67b"
        private const val IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload"

        // Maximum file size (32MB for ImgBB)
        private const val MAX_FILE_SIZE = 32 * 1024 * 1024
    }

    /**
     * Upload image to ImgBB and get CDN URL
     */
    suspend fun uploadImage(imageUri: Uri): ImageUploadResult {
        return withContext(Dispatchers.IO) {
            try {
                // Convert URI to Base64
                val base64Image = convertUriToBase64(imageUri)
                if (base64Image == null) {
                    return@withContext ImageUploadResult(
                        isSuccess = false,
                        errorMessage = "Failed to process image"
                    )
                }

                // Check file size
                if (base64Image.length > MAX_FILE_SIZE) {
                    return@withContext ImageUploadResult(
                        isSuccess = false,
                        errorMessage = "Image size too large. Please compress the image."
                    )
                }

                // Upload to ImgBB
                uploadToImgBB(base64Image)

            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image", e)
                ImageUploadResult(
                    isSuccess = false,
                    errorMessage = "Upload failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Convert URI to Base64 string
     */
    private fun convertUriToBase64(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.use { stream ->
                val buffer = ByteArrayOutputStream()
                val data = ByteArray(1024)
                var nRead: Int
                while (stream.read(data, 0, data.size).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.toByteArray()
            }

            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else null

        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to Base64", e)
            null
        }
    }

    /**
     * Upload Base64 image to ImgBB
     */
    private suspend fun uploadToImgBB(base64Image: String): ImageUploadResult {
        return try {
            val url = URL(IMGBB_UPLOAD_URL)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true

            // Create form data
            val postData = buildString {
                append("key=").append(URLEncoder.encode(IMGBB_API_KEY, "UTF-8"))
                append("&image=").append(URLEncoder.encode(base64Image, "UTF-8"))
                append("&expiration=").append(URLEncoder.encode("600", "UTF-8")) // Optional: 10 minutes expiry
            }

            // Send request
            connection.outputStream.use { outputStream ->
                outputStream.write(postData.toByteArray())
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = json.decodeFromString<ImgBBUploadResponse>(responseBody)

                if (response.success && response.data != null) {
                    Log.d(TAG, "Image uploaded successfully: ${response.data.url}")
                    ImageUploadResult(
                        isSuccess = true,
                        imageUrl = response.data.url
                    )
                } else {
                    val errorMsg = response.error?.message ?: "Upload failed"
                    Log.e(TAG, "ImgBB upload failed: $errorMsg")
                    ImageUploadResult(
                        isSuccess = false,
                        errorMessage = errorMsg
                    )
                }
            } else {
                Log.e(TAG, "HTTP error: $responseCode - $responseBody")
                ImageUploadResult(
                    isSuccess = false,
                    errorMessage = "Upload service error: HTTP $responseCode"
                )
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error uploading to ImgBB", e)
            ImageUploadResult(
                isSuccess = false,
                errorMessage = "Network error: ${e.message}"
            )
        }
    }
}