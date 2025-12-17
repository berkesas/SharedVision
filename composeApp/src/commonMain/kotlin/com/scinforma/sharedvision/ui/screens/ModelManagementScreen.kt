package com.scinforma.sharedvision.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scinforma.sharedvision.data.model.ImageModel
import com.scinforma.sharedvision.ui.icons.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementScreen() {
    var models by remember { mutableStateOf(getSampleModels()) }
    var showAddModelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Models",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${models.count { it.isDownloaded }} of ${models.size} models downloaded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FloatingActionButton(
                onClick = { showAddModelDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = getAddIcon(),
                    contentDescription = "Add Model"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Storage Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getStorageIcon(),
                    contentDescription = "Storage Used",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Storage Used",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "234 MB of 2.1 GB available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                LinearProgressIndicator(
                    progress = { 0.11f },
                    modifier = Modifier.width(80.dp),
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        val filters = listOf("All", "Downloaded", "Available", "Recent")
        var selectedFilter by remember { mutableStateOf("All") }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    selected = selectedFilter == filter
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Models List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(models) { model ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = model.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simple action buttons
                        Row {
                            if (model.isDownloaded) {
                                OutlinedButton(onClick = { /* Delete */ }) {
                                    Text("Delete")
                                }
                            } else {
                                Button(onClick = { /* Download */ }) {
                                    Text("Download")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Model Dialog
    if (showAddModelDialog) {
        var modelName by remember { mutableStateOf("") }
        var modelUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddModelDialog = false },
            title = { Text("Add Custom Model") },
            text = {
                Column {
                    OutlinedTextField(
                        value = modelName,
                        onValueChange = { modelName = it },
                        label = { Text("Model Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelUrl,
                        onValueChange = { modelUrl = it },
                        label = { Text("Download URL") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (modelName.isNotBlank() && modelUrl.isNotBlank()) {
                            showAddModelDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddModelDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Sample data function
private fun getSampleModels(): List<ImageModel> {
    return listOf(
        ImageModel(
            id = "1",
            name = "MobileNet v2",
            description = "Efficient CNN for image classification",
            version = "2.0",
            size = 14L * 1024 * 1024,
            downloadUrl = "https://example.com/mobilenet_v2.tflite",
            fileName = "mobilenet_v2.tflite",
            isDownloaded = true,
            accuracy = 0.71f
        ),
        ImageModel(
            id = "2",
            name = "EfficientNet B0",
            description = "State-of-the-art accuracy",
            version = "1.0",
            size = 21L * 1024 * 1024,
            downloadUrl = "https://example.com/efficientnet_b0.tflite",
            fileName = "efficientnet_b0.tflite",
            isDownloaded = false,
            accuracy = 0.77f
        )
    )
}