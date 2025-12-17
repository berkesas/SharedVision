package com.scinforma.sharedvision.ml

import androidx.compose.ui.graphics.ImageBitmap
import com.scinforma.sharedvision.utils.Logger

private const val TAG = "ModelManager"
/**
 * Singleton manager for ML model lifecycle
 * Ensures model loads once and persists throughout app session
 */
object ModelManager {
    private var classifier: ImageClassifier? = null
    private var isInitialized = false
    private var isModelLoaded = false

    private var currentModelFolder = "manat"
    private var currentLanguageCode = "en"

    /**
     * Initialize the classifier with platform-specific context
     * This should be called once from the Application class or main activity
     */
    fun initialize(context: Any, modelPath: String, languageCode: String): Boolean {
        Logger.d(TAG, "Initializing model $modelPath")
        return try {
            // Check if reinitialization is needed (first time or model changed)
            if (!isInitialized || currentModelFolder != modelPath) {
                // Clean up existing classifier if reinitializing
                if (isInitialized) {
                    Logger.d(TAG, "Model changed from $currentModelFolder to $modelPath, reinitializing...")
                    classifier?.close() // Add this if your classifier has a close/cleanup method
                    classifier = null
                }

                currentModelFolder = modelPath
                currentLanguageCode = languageCode
                classifier = createPlatformClassifier(context)
                isModelLoaded = classifier?.loadModel(currentModelFolder, languageCode) ?: false
                isInitialized = true

                println("ModelManager: Initialized - Model loaded: $isModelLoaded")
            } else {
                Logger.d(TAG, "Model already initialized with same path: $modelPath")
            }
            isModelLoaded
        } catch (e: Exception) {
            println("ModelManager: Initialization failed: ${e.message}")
            isInitialized = false
            isModelLoaded = false
            false
        }
    }

    /**
     * Get the singleton classifier instance
     * Returns null if not initialized
     */
    fun getClassifier(): ImageClassifier? = classifier

    /**
     * Check if model is ready for classification
     */
    fun isModelReady(): Boolean {
        return isInitialized && isModelLoaded && classifier?.isModelLoaded() == true
    }


    /**
     * Classify an image using the singleton classifier
     */
    fun classifyImage(imageBitmap: ImageBitmap, context: Any): ClassificationResult {
        val classifier = this.classifier
        return if (classifier != null && isModelReady()) {
            try {
                classifier.classifyImage(imageBitmap, context)
            } catch (e: Exception) {
                println("ModelManager: Classification error: ${e.message}")
                ClassificationResult("Classification failed", 0f)
            }
        } else {
            ClassificationResult("Model not initialized", 0f)
        }
    }

    /**
     * Force reload the model (for error recovery)
     */
    fun reloadModel(): Boolean {
        return try {
            val success = classifier?.loadModel(currentModelFolder, currentLanguageCode) ?: false
            isModelLoaded = success
            println("ModelManager: Model reload result: $success")
            success
        } catch (e: Exception) {
            println("ModelManager: Model reload failed: ${e.message}")
            isModelLoaded = false
            false
        }
    }

    /**
     * Cleanup resources (call from Application.onTerminate or activity onDestroy)
     */
    fun cleanup() {
        try {
            classifier?.close()
            classifier = null
            isInitialized = false
            isModelLoaded = false
            println("ModelManager: Cleanup completed")
        } catch (e: Exception) {
            println("ModelManager: Cleanup error: ${e.message}")
        }
    }
}