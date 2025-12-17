package com.scinforma.sharedvision.ml

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.scinforma.sharedvision.utils.Logger
import kotlinx.coroutines.tasks.await

/**
 * Text Recognition Manager
 * Handles ML Kit text recognition operations
 */
object TextRecognitionManager {
    private const val TAG = "TextRecognitionManager"

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Recognition result data class
     */
    data class RecognitionResult(
        val text: String,
        val blocks: List<TextBlock> = emptyList()
    )

    /**
     * Text block data class for structured results
     */
    data class TextBlock(
        val text: String,
        val lines: List<String>
    )

    /**
     * Recognize text from ImageBitmap
     */
    suspend fun recognizeText(imageBitmap: ImageBitmap): RecognitionResult {
        return try {
            Logger.d(TAG, "Starting text recognition")

            // Convert ImageBitmap to Android Bitmap
            val bitmap = imageBitmap.asAndroidBitmap()

            // Create InputImage from Bitmap
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            // Process image
            val visionText = textRecognizer.process(inputImage).await()

            val recognizedText = visionText.text
            Logger.i(TAG, "Text recognition completed. Characters: ${recognizedText.length}")

            // Extract text blocks for structured data
            val blocks = visionText.textBlocks.map { block ->
                TextBlock(
                    text = block.text,
                    lines = block.lines.map { it.text }
                )
            }

            RecognitionResult(
                text = recognizedText,
                blocks = blocks
            )
        } catch (e: Exception) {
            Logger.e(TAG, "Text recognition failed", e)
            RecognitionResult(
                text = "",
                blocks = emptyList()
            )
        }
    }

    /**
     * Close the text recognizer
     */
    fun close() {
        try {
            textRecognizer.close()
            Logger.d(TAG, "Text recognizer closed")
        } catch (e: Exception) {
            Logger.e(TAG, "Error closing text recognizer", e)
        }
    }
}