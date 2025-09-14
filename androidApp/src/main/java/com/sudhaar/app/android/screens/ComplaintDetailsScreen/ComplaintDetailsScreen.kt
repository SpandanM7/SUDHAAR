package com.sudhaar.app.android.screens.ComplaintDetailsScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailsScreen(
    complaintType: String,
    onBackPressed: () -> Unit,
    onSubmit: (Uri?, String, String) -> Unit // Added selectedCategory parameter
) {
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf<String?>(null) }

    // Dropdown states
    var selectedCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val categoryOptions = listOf(
        "Road Condition",
        "Garbage Mismanagement",
        "Water Supply Issues",
        "Electricity Problems",
        "Drainage Issues",
        "Street Lighting",
        "Public Transport",
        "Noise Pollution",
        "Illegal Construction",
        "Other"
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        imageError = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0C0C0C) // Deep black background
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
                IconButton(onClick = onBackPressed) {
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
                // Image Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF111111) // Flat dark card background
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
                                    tint = Color(0xFF00CEC8), // Wine accent color
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
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(
                                        "Change Image",
                                        color = Color(0xFF00CEC8) // Wine accent color
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { launcher.launch("image/*") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    border = BorderStroke(
                                        2.dp,
                                        if (imageError != null) Color(0xFFCF6679)
                                        else Color(0xFF79DB70).copy(alpha = 0.5f) // Wine accent color
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
                                            tint = Color(0xFF79DB70) // Wine accent color
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
                                    color = Color(0xFFCF6679), // Error color
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
                                onExpandedChange = { categoryExpanded = !categoryExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Select complaint category", color = Color.White.copy(alpha = 0.6f)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF1F1F1F),
                                        unfocusedContainerColor = Color(0xFF1F1F1F),
                                        unfocusedBorderColor = Color(0xFF79DB70).copy(alpha = 0.5f),
                                        focusedBorderColor = Color(0xFF79DB70),
                                        cursorColor = Color(0xFF79DB70),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
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
                                    // Set the background color of the dropdown menu itself
                                    modifier = Modifier.background(Color(0xFF1F1F1F))
                                ) {
                                    categoryOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(text = option, color = Color.White) },
                                            onClick = {
                                                selectedCategory = option
                                                categoryExpanded = false
                                                categoryError = null
                                            },
                                            // Set the background color for each individual menu item
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
                                    if (newValue.length <= 500) {
                                        description = newValue
                                        descriptionError = null
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Please provide detailed description of the issue...", color = Color.White.copy(alpha = 0.6f)) },
                                minLines = 4,
                                maxLines = 8,
                                isError = descriptionError != null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1F1F1F),
                                    unfocusedContainerColor = Color(0xFF1F1F1F),
                                    unfocusedBorderColor = Color(0xFF79DB70).copy(alpha = 0.5f),
                                    focusedBorderColor = Color(0xFF79DB70),
                                    cursorColor = Color(0xFF79DB70),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
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
                        onClick = {
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

                            if (!hasErrors) {
                                onSubmit(selectedImageUri, description.trim(), selectedCategory)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(bottom = 16.dp),
                        enabled = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF79DB70) // Wine accent for button
                        )
                    ) {
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