package com.scinforma.sharedvision.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scinforma.sharedvision.ml.ClassificationResult
import com.scinforma.sharedvision.ml.ModelManager
import com.scinforma.sharedvision.ui.camera.CameraPreview
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.scinforma.sharedvision.data.ILanguagePreferences
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import com.scinforma.sharedvision.utils.Logger
import com.scinforma.sharedvision.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import com.scinforma.sharedvision.services.TTSManager
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.scinforma.sharedvision.generated.resources.classification_failed
import com.scinforma.sharedvision.generated.resources.processing
import com.scinforma.sharedvision.generated.resources.confidence
import com.scinforma.sharedvision.generated.resources.top_predictions
import com.scinforma.sharedvision.generated.resources.prediction_item
import com.scinforma.sharedvision.generated.resources.percentage
import com.scinforma.sharedvision.generated.resources.loading_model
import com.scinforma.sharedvision.generated.resources.camera_permission_required
import com.scinforma.sharedvision.generated.resources.point_camera
import com.scinforma.sharedvision.generated.resources.mode_manual
import com.scinforma.sharedvision.generated.resources.mode_automatic
import com.scinforma.sharedvision.generated.resources.recognize
import com.scinforma.sharedvision.generated.resources.camera_permission_message
import com.scinforma.sharedvision.generated.resources.grant_camera_permission

private const val TAG = "ModelRunnerScreen"

// Platform-specific functions
expect fun checkCameraPermission(context: Any): Boolean

@Composable
expect fun RequestCameraPermission(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit

@Composable
expect fun getLocalContext(): Any

expect suspend fun saveImageForTesting(context: Any, imageBitmap: ImageBitmap, filename: String): String?

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ModelRunnerScreen(
    languagePreferences: ILanguagePreferences, modelPath: String="manat"
) {
    val context = LocalContext.current
    var classificationResult by remember { mutableStateOf<ClassificationResult?>(null) }
    var isAutoMode by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var currentImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var frameCounter by remember { mutableStateOf(0) }
    var imageCounter by remember { mutableStateOf(0) }
    val manualCaptureRequested = remember { mutableStateOf(false) }

    // Collect settings
    val autoReadInterval by languagePreferences.autoReadIntervalFlow.collectAsState(initial = 2000L)
    val ttsEnabled by languagePreferences.ttsEnabledFlow.collectAsState(initial = true)

    // Track last classification time
    var lastClassificationTime by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    // Get localized strings
    val classificationFailedText = stringResource(Res.string.classification_failed)

    fun performClassification(imageBitmap: ImageBitmap, mode: String){
        Logger.d(TAG, "performClassification called - mode: $mode, isProcessing: $isProcessing")

        if (isProcessing) {
            Logger.d(TAG, "Skipping classification - already processing")
            return
        }

        coroutineScope.launch {
            isProcessing = true
            try {
                Logger.d(TAG, "$mode classification start - Frame #$frameCounter")

                val filename = "test_image_${mode}_${imageCounter++}.jpg"
                saveImageForTesting(context, imageBitmap, filename)

                val result = ModelManager.classifyImage(imageBitmap, context)
                classificationResult = result
                Logger.i(TAG, "$mode classification complete - ${result.topPrediction}")

                // Speak the result only if TTS is enabled - using TTSManager
                if (ttsEnabled) {
                    TTSManager.speak(result.topPrediction)
                }
            } catch (e: Exception) {
                Logger.e(TAG, "$mode classification failed", e)
                classificationResult = ClassificationResult(classificationFailedText, 0f)
            } finally {
                isProcessing = false
            }
        }
    }

    LaunchedEffect(classificationResult) {
        println("DEBUG: UI received new classification result: ${classificationResult?.topPrediction}")
        classificationResult?.allPredictions?.take(3)?.forEachIndexed { index, pred ->
            println("DEBUG: Prediction $index: ${pred.label} (${(pred.confidence * 100).toInt()}%)")
        }
    }

    // Camera permission state
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Check initial permission state
    LaunchedEffect(Unit) {
        hasCameraPermission = checkCameraPermission(context)

        TTSManager.initialize(context, languagePreferences)
    }

    // Permission request launcher
    val requestPermission = RequestCameraPermission { isGranted ->
        hasCameraPermission = isGranted
    }

    // Track model loading state locally
    var isModelLoaded by remember { mutableStateOf(false) }

    // Initialize ModelManager and load model on startup
//    LaunchedEffect(Unit) {
//        println("DEBUG: Starting ModelManager initialization...")
//        try {
//
//            val success = ModelManager.initialize(context, modelPath)
//            println("DEBUG: ModelManager initialization result: $success")
//            isModelLoaded = ModelManager.isModelReady()
//            println("DEBUG: Model ready state: $isModelLoaded")
//        } catch (e: Exception) {
//            println("DEBUG: Exception during ModelManager initialization: ${e.message}")
//            isModelLoaded = false
//        }
//    }

    val labelLanguage by languagePreferences.labelLanguageFlow.collectAsState(initial = "en")

    // Re-initialize ModelManager when modelPath changes
    LaunchedEffect(modelPath, labelLanguage) {
        Log.d(TAG, "modelPath or labelLanguage changed - reinitializing ModelManager...")
        Log.d(TAG, "modelPath: $modelPath, labelLanguage: $labelLanguage")
        isModelLoaded = false
        classificationResult = null // Clear previous results

        try {
            val success = ModelManager.initialize(context, modelPath, labelLanguage)
            Log.d(TAG, "ModelManager reinitialization result: $success")
            isModelLoaded = ModelManager.isModelReady()
            Log.d(TAG, "Model ready state after change: $isModelLoaded")
        } catch (e: Exception) {
            Log.d(TAG, "Exception during ModelManager reinitialization: ${e.message}")
            isModelLoaded = false
        }
    }

    // Automatic classification with interval control
    LaunchedEffect(frameCounter, isAutoMode, isModelLoaded, autoReadInterval) {
        if (isAutoMode && currentImageBitmap != null && isModelLoaded && !isProcessing) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastClassification = currentTime - lastClassificationTime

            if (timeSinceLastClassification >= autoReadInterval) {
                currentImageBitmap?.let { imageBitmap ->
                    lastClassificationTime = currentTime
                    performClassification(imageBitmap, "auto")
                }
            }
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
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
                                val frameNum = frameCounter + 1

                                // Convert to bitmap and close ImageProxy immediately
                                val imageBitmap = cameraImage.toBitmap()
                                cameraImage.close()

                                // Store the bitmap
                                currentImageBitmap = imageBitmap
                                frameCounter = frameNum

                                // Check if manual capture is requested
                                if (manualCaptureRequested.value) {
                                    manualCaptureRequested.value = false
                                    performClassification(imageBitmap, "manual")
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
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(Res.string.processing),
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Cards Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Prediction Results
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (classificationResult != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = classificationResult!!.topPrediction,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            if (classificationResult!!.allPredictions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))

                                classificationResult!!.allPredictions.take(3).forEachIndexed { index, prediction ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${index + 1}. ${prediction.label}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = "${(prediction.confidence * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                    if (index < 2) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = when {
                                !isModelLoaded -> stringResource(Res.string.loading_model)
                                !hasCameraPermission -> stringResource(Res.string.camera_permission_required)
                                else -> stringResource(Res.string.point_camera)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.mode_manual),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (!isAutoMode) Color.White
                            else Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = isAutoMode,
                            onCheckedChange = { isAutoMode = it },
                            enabled = hasCameraPermission && isModelLoaded
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.mode_automatic),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isAutoMode) Color.White
                            else Color.White.copy(alpha = 0.6f)
                        )
                    }

                    // Recognize Button
                    Button(
                        onClick = {
                            Logger.d(TAG, "Button clicked - isAutoMode: $isAutoMode, isProcessing: $isProcessing")
                            if (!isProcessing && !isAutoMode) {
                                Logger.d(TAG, "Setting manualCaptureRequested to true")
                                manualCaptureRequested.value = true
                                Logger.d(TAG, "manualCaptureRequested is now: ${manualCaptureRequested.value}")
                            } else {
                                Logger.d(TAG, "Button click ignored - conditions not met")
                            }
                        },
                        enabled = !isAutoMode &&
                                !isProcessing &&
                                isModelLoaded &&
                                hasCameraPermission
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(stringResource(Res.string.recognize))
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
            text = stringResource(Res.string.camera_permission_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRequestPermission
        ) {
            Text(stringResource(Res.string.grant_camera_permission))
        }
    }
}