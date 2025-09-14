package com.sudhaar.app.android.screens.ComplaintDetailsScreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sudhaar.app.android.utils.ApiService
import com.sudhaar.app.android.utils.ImageUploadService
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ComplaintRequest(
    val title: String,
    val type: String,
    val priority: Int = 1,
    val location: DoubleArray,
    val zip: Int = 700090,
    val status: String = "unassigned",
    val images: List<String>,
    val created: String
) {
    // Required for DoubleArray in data classes
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ComplaintRequest
        return location.contentEquals(other.location) &&
                title == other.title &&
                type == other.type &&
                priority == other.priority &&
                zip == other.zip &&
                status == other.status &&
                images == other.images &&
                created == other.created
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + priority
        result = 31 * result + location.contentHashCode()
        result = 31 * result + zip
        result = 31 * result + status.hashCode()
        result = 31 * result + images.hashCode()
        result = 31 * result + created.hashCode()
        return result
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailsScreen(
    complaintType: String,
    onBackPressed: () -> Unit,
    onSubmit: (Uri?, String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = remember { ApiService(context) }
    val imageUploadService = remember { ImageUploadService(context) }

    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }

    // Dropdown states
    var selectedCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val categoryOptions = listOf(
        "Road_Condition",
        "Garbage_Mismanagement",
        "Water_Supply_Issues",
        "Electricity_Problems",
        "Drainage_Issues",
        "Street_Lighting",
        "Public_Transport",
        "Noise_Pollution",
        "Illegal_Construction",
        "Other"
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        imageError = null
    }

    // JSON configuration for proper serialization
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    // Function to handle complaint submission
    fun submitComplaint() {
        scope.launch {
            isLoading = true
            submitError = null

            try {
                // First validate all fields
                var hasErrors = false

                // Validate image
                if (selectedImageUri == null) {
                    imageError = "Please add an image"
                    hasErrors = true
                }

                // Validate category
                if (selectedCategory.isEmpty()) {
                    categoryError = "Please select a category"
                    hasErrors = true
                }

                // Validate description
                if (description.trim().isEmpty()) {
                    descriptionError = "Please describe the issue"
                    hasErrors = true
                } else if (description.trim().length < 10) {
                    descriptionError = "Please provide more details (minimum 10 characters)"
                    hasErrors = true
                }

                if (hasErrors) {
                    isLoading = false
                    return@launch
                }

                // Upload image first
                Log.d("ComplaintSubmission", "Starting image upload...")
                val uploadResult = imageUploadService.uploadImage(selectedImageUri!!)

                if (!uploadResult.isSuccess) {
                    submitError = uploadResult.errorMessage ?: "Failed to upload image"
                    isLoading = false
                    return@launch
                }

                val imageUrl = uploadResult.imageUrl
                if (imageUrl.isNullOrEmpty()) {
                    submitError = "Failed to get image URL"
                    isLoading = false
                    return@launch
                }

                Log.d("ComplaintSubmission", "Image uploaded successfully: $imageUrl")

                // Create complaint request
                val currentTime = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                val complaintRequest = ComplaintRequest(
                    title = description.trim(),
                    type = selectedCategory,
                    priority = 1,
                    location = doubleArrayOf(50.23, 65.12),
                    zip = 700090,
                    status = "unassigned",
                    images = listOf(imageUrl),
                    created = currentTime
                )

                val requestBody = json.encodeToString(complaintRequest)
                Log.d("ComplaintSubmission", "Submitting complaint JSON: $requestBody")

                // Submit complaint to backend
                val result = apiService.post("https://sudhaar-backend-mob.onrender.com/addcomplaint", requestBody)
                //val result = apiService.post("http://192.168.1.5:3000/addcomplaint", requestBody)
                if (result.isSuccess) {
                    Log.d("ComplaintSubmission", "Complaint submitted successfully")
                    onSubmit(selectedImageUri, description.trim(), selectedCategory)
                } else {
                    submitError = result.errorMessage ?: "Failed to submit complaint"
                    Log.e("ComplaintSubmission", "Error: ${result.errorMessage}")
                }

            } catch (e: Exception) {
                submitError = "Unexpected error: ${e.message}"
                Log.e("ComplaintSubmission", "Exception during submission", e)
            } finally {
                isLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0C0C0C)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackPressed,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = "Lodge Complaint",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Loading/Error State
                if (submitError != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2D1B1B)
                            )
                        ) {
                            Text(
                                text = submitError!!,
                                color = Color(0xFFCF6679),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Image Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF111111)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF00CEC8),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Add Image *",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                TextButton(
                                    onClick = { launcher.launch("image/*") },
                                    modifier = Modifier.padding(top = 8.dp),
                                    enabled = !isLoading
                                ) {
                                    Text(
                                        "Change Image",
                                        color = Color(0xFF00CEC8)
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { launcher.launch("image/*") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    enabled = !isLoading,
                                    border = BorderStroke(
                                        2.dp,
                                        if (imageError != null) Color(0xFFCF6679)
                                        else Color(0xFF79DB70).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = Color(0xFF79DB70)
                                        )
                                        Text(
                                            text = "Tap to add image",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 8.dp),
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            if (imageError != null) {
                                Text(
                                    text = imageError!!,
                                    color = Color(0xFFCF6679),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Category Dropdown Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF111111)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = Color(0xFF00CEC8),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Select Category *",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            ExposedDropdownMenuBox(
                                expanded = categoryExpanded,
                                onExpandedChange = {
                                    if (!isLoading) {
                                        categoryExpanded = !categoryExpanded
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory.replace("_", " "),
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = !isLoading,
                                    placeholder = { Text("Select complaint category", color = Color.White.copy(alpha = 0.6f)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF1F1F1F),
                                        unfocusedContainerColor = Color(0xFF1F1F1F),
                                        disabledContainerColor = Color(0xFF1F1F1F),
                                        unfocusedBorderColor = Color(0xFF79DB70).copy(alpha = 0.5f),
                                        focusedBorderColor = Color(0xFF79DB70),
                                        disabledBorderColor = Color(0xFF79DB70).copy(alpha = 0.3f),
                                        cursorColor = Color(0xFF79DB70),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        disabledTextColor = Color.White.copy(alpha = 0.6f),
                                        errorBorderColor = Color(0xFFCF6679)
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    isError = categoryError != null
                                )

                                ExposedDropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false },
                                    modifier = Modifier.background(Color(0xFF1F1F1F))
                                ) {
                                    categoryOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(text = option.replace("_", " "), color = Color.White) },
                                            onClick = {
                                                selectedCategory = option
                                                categoryExpanded = false
                                                categoryError = null
                                            },
                                            modifier = Modifier.background(Color(0xFF1F1F1F))
                                        )
                                    }
                                }
                            }

                            if (categoryError != null) {
                                Text(
                                    text = categoryError!!,
                                    color = Color(0xFFCF6679),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Description Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF111111)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF00CEC8),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Describe Your Issue *",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            OutlinedTextField(
                                value = description,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 500 && !isLoading) {
                                        description = newValue
                                        descriptionError = null
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                placeholder = { Text("Please provide detailed description of the issue...", color = Color.White.copy(alpha = 0.6f)) },
                                minLines = 4,
                                maxLines = 8,
                                isError = descriptionError != null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1F1F1F),
                                    unfocusedContainerColor = Color(0xFF1F1F1F),
                                    disabledContainerColor = Color(0xFF1F1F1F),
                                    unfocusedBorderColor = Color(0xFF79DB70).copy(alpha = 0.5f),
                                    focusedBorderColor = Color(0xFF79DB70),
                                    disabledBorderColor = Color(0xFF79DB70).copy(alpha = 0.3f),
                                    cursorColor = Color(0xFF79DB70),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White.copy(alpha = 0.6f),
                                    errorBorderColor = Color(0xFFCF6679)
                                ),
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = descriptionError ?: "",
                                            color = if (descriptionError != null) Color(0xFFCF6679)
                                            else Color.Transparent
                                        )
                                        Text(
                                            text = "${description.length}/500",
                                            color = if (description.length > 450) Color(0xFFCF6679).copy(alpha = 0.7f)
                                            else Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Submit Button
                item {
                    Button(
                        onClick = { submitComplaint() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(bottom = 16.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF79DB70),
                            disabledContainerColor = Color(0xFF79DB70).copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Submitting...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Submit Complaint",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}