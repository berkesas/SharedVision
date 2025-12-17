package com.scinforma.sharedvision.ml

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Common class for image classification across platforms
 */
expect class ImageClassifier {
    fun loadModel(modelPath: String, languageCode: String): Boolean
    fun isModelLoaded(): Boolean
    fun classifyImage(imageBitmap: ImageBitmap, context: Any): ClassificationResult
    fun close()
}

/**
 * Data class representing a single prediction
 */
data class Prediction(
    val label: String,
    val confidence: Float
)

/**
 * Data class representing classification results
 */
data class ClassificationResult(
    val topPrediction: String,
    val confidence: Float,
    val allPredictions: List<Prediction> = emptyList()
)

/**
 * Platform-specific classifier creation function
 * This is expected to be implemented in each platform's source set
 */
expect fun createPlatformClassifier(context: Any): ImageClassifier