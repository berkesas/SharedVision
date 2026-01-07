package com.scinforma.sharedvision.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import android.content.Context
import com.scinforma.sharedvision.data.IUserPreferences
import com.scinforma.sharedvision.ml.TextRecognitionManager
import com.scinforma.sharedvision.services.TTSManager
import com.scinforma.sharedvision.ui.camera.CameraPreview
import com.scinforma.sharedvision.utils.Logger
import kotlinx.coroutines.launch
import com.scinforma.sharedvision.generated.resources.*
import org.jetbrains.compose.resources.stringResource

private const val TAG = "TextRecognizerScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextRecognizerScreen(
    navController: NavController,
    userPreferences: IUserPreferences
) {
    val context = LocalContext.current as Context
    var isProcessing by remember { mutableStateOf(false) }
    var currentImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val manualCaptureRequested = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Camera permission state
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Check initial permission state
    LaunchedEffect(Unit) {
        hasCameraPermission = checkCameraPermission(context)
        // Initialize TTS
        TTSManager.initialize(context, userPreferences)
    }

    // Permission request launcher
    val requestPermission = RequestCameraPermission { isGranted ->
        hasCameraPermission = isGranted
    }

    fun performTextRecognition(imageBitmap: ImageBitmap) {
        Logger.d(TAG, "performTextRecognition called, isProcessing: $isProcessing")

        if (isProcessing) {
            Logger.d(TAG, "Skipping recognition - already processing")
            return
        }

        coroutineScope.launch {
            isProcessing = true
            try {
                Logger.d(TAG, "Starting text recognition")

                val result = TextRecognitionManager.recognizeText(imageBitmap)

                Logger.i(TAG, "Text recognition complete. Text length: ${result.text.length}")

                if (result.text.isNotEmpty()) {
                    // Navigate to recognized text screen with the result
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "recognized_text",
                        result.text
                    )
                    navController.navigate("recognized_text")
                } else {
                    Logger.w(TAG, "No text recognized")
                    // Could show a snackbar or toast here
                }
            } catch (e: Exception) {
                Logger.e(TAG, "Text recognition failed", e)
            } finally {
                isProcessing = false
            }
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            // Don't shutdown TTS here as it might be used in other screens
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Camera Preview - Full Screen Background
        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!hasCameraPermission) {
                    CameraPermissionContent(
                        onRequestPermission = requestPermission
                    )
                } else {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onImageCaptured = { cameraImage ->
                            try {
                                // Convert to bitmap and close ImageProxy immediately
                                val imageBitmap = cameraImage.toBitmap()
                                cameraImage.close()

                                // Store the bitmap
                                currentImageBitmap = imageBitmap

                                // Check if manual capture is requested
                                if (manualCaptureRequested.value) {
                                    manualCaptureRequested.value = false
                                    performTextRecognition(imageBitmap)
                                }
                            } catch (e: Exception) {
                                Logger.e(TAG, "Failed to handle camera image", e)
                                cameraImage.close()
                            }
                        }
                    )

                    // Processing overlay
                    if (isProcessing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(Res.string.recognizing),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Control Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!hasCameraPermission) {
                        Text(
                            text = "Camera permission required",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.point_camera),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recognize Button
                    Button(
                        onClick = {
                            Logger.d(TAG, "Recognize button clicked")
                            if (!isProcessing) {
                                Logger.d(TAG, "Setting manualCaptureRequested to true")
                                manualCaptureRequested.value = true
                            }
                        },
                        enabled = !isProcessing && hasCameraPermission,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isProcessing) stringResource(Res.string.recognizing) else stringResource(Res.string.recognize),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPermissionContent(
    onRequestPermission: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Camera permission is required to recognize text",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRequestPermission
        ) {
            Text("Grant Camera Permission")
        }
    }
}