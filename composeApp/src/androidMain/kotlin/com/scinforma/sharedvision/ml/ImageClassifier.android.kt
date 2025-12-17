package com.scinforma.sharedvision.ml

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.scinforma.sharedvision.AppConfig
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp
import android.util.Log

private const val TAG = "ImageClassifier"
/**
 * Android-specific implementation of ImageClassifier using TensorFlow Lite
 */
actual class ImageClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private var isLoaded = false

    // Model input/output configurations
    private val inputSize = 224 // Typical size for image classification models
    private val pixelSize = 3 // RGB
    // Remove the pre-allocated buffer - we'll create it fresh each time
    // private val imageBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
    //     .apply { order(ByteOrder.nativeOrder()) }

    actual fun loadModel(modelPath: String, languageCode: String): Boolean {
        return try {
            Log.i(TAG,"AndroidImageClassifier: Loading model from $modelPath")

            // Load the TFLite model from assets
            val modelBuffer = loadModelFile(modelPath)
            interpreter = Interpreter(modelBuffer)

            // Load labels if they exist (with language support)
            loadLabels(modelPath, languageCode)

            isLoaded = true
            Log.i(TAG,"ImageClassifier: Model loaded successfully. Input shape: ${interpreter?.getInputTensor(0)?.shape()?.contentToString()}")
            true
        } catch (e: IOException) {
            Log.i(TAG,"ImageClassifier: Failed to load model: ${e.message}")
            isLoaded = false
            false
        } catch (e: Exception) {
            Log.i(TAG,"ImageClassifier: Unexpected error loading model: ${e.message}")
            isLoaded = false
            false
        }
    }

    actual fun isModelLoaded(): Boolean {
        return isLoaded && interpreter != null
    }

    // Separate function for saving processed bitmap - easy to disable for production
    private fun saveProcessedBitmapForTesting(processedBitmap: Bitmap, context: Any?) {
        if (!AppConfig.SAVE_PROCESSED_IMAGES) {
            return
        }

        context?.let { ctx ->
            try {
                val androidContext = ctx as android.content.Context
                val testDir = File(androidContext.getExternalFilesDir(null), "processed_images")
                if (!testDir.exists()) {
                    testDir.mkdirs()
                }

                val timestamp = System.currentTimeMillis()
                val imageFile = File(testDir, "processed_${timestamp}.jpg")

                FileOutputStream(imageFile).use { out ->
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }

                Log.d(TAG,"Processed image saved to: ${imageFile.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG,"Failed to save processed image: ${e.message}")
            }
        }
    }

    actual fun classifyImage(imageBitmap: ImageBitmap, context: Any): ClassificationResult {
        val interpreter = this.interpreter
        if (!isLoaded || interpreter == null) {
            return ClassificationResult("Model not loaded", 0f)
        }

        return try {
            // Convert ImageBitmap to Android Bitmap
            val androidBitmap = imageBitmap.asAndroidBitmap()

            // Center crop and resize the image
            val processedBitmap = centerCropAndResize(androidBitmap, inputSize)

            // Save processed bitmap for testing (comment out this line to disable)
            saveProcessedBitmapForTesting(processedBitmap, context)

            val imageBuffer = convertBitmapToByteBuffer(processedBitmap)

            // Debug: Log some pixel values to verify buffer changes
            imageBuffer.rewind()
            val samplePixels = FloatArray(9) // Sample first 3 pixels (9 values)
            imageBuffer.asFloatBuffer().get(samplePixels)
            Log.i(TAG,"Sample pixel values: ${samplePixels.take(6).joinToString(", ")}")
            imageBuffer.rewind()

            // Clean up processed bitmap to prevent memory leaks
            if (processedBitmap != androidBitmap) {
                processedBitmap.recycle()
            }

            // Handle quantized models if needed
            val inputTensor = interpreter.getInputTensor(0)
            if (inputTensor.dataType() == org.tensorflow.lite.DataType.UINT8) {
                Log.i(TAG,"Detected quantized model, applying quantization")
                val quantizationParams = inputTensor.quantizationParams()
                val scale = quantizationParams.scale
                val zeroPoint = quantizationParams.zeroPoint

                // Convert float values to quantized uint8
                val floatBuffer = imageBuffer.asFloatBuffer()
                imageBuffer.rewind()

                for (i in 0 until floatBuffer.capacity()) {
                    val floatVal = floatBuffer.get(i)
                    val quantizedVal = ((floatVal / scale) + zeroPoint).toInt().coerceIn(0, 255)
                    imageBuffer.put(i * 4, quantizedVal.toByte())
                }
                imageBuffer.rewind()
            } else {
                Log.i(TAG,"Using float32 model (no quantization needed)")
            }

            // Run inference - get the actual output size from the model
            val outputTensor = interpreter.getOutputTensor(0)
            val outputShape = outputTensor.shape()
            val outputSize = outputShape[1] // Should be 12 based on your error

            Log.i(TAG,"Model output shape: ${outputShape.contentToString()}")

            val output = Array(1) { FloatArray(outputSize) }
            interpreter.run(imageBuffer, output)

            // Process results
            val predictions = processOutput(output[0])

            if (predictions.isNotEmpty()) {
                ClassificationResult(
                    topPrediction = predictions[0].label,
                    confidence = predictions[0].confidence,
                    allPredictions = predictions
                )
            } else {
                ClassificationResult("No predictions", 0f)
            }

        } catch (e: Exception) {
            Log.i(TAG,"Classification error: ${e.message}")
            ClassificationResult("Classification error: ${e.message}", 0f)
        }
    }

    actual fun close() {
        try {
            interpreter?.close()
            interpreter = null
            isLoaded = false
            Log.i(TAG,"Resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG,"Error during cleanup: ${e.message}")
        }
    }

    private fun loadLabels(modelPath: String, languageCode: String = "en") {
        try {
            // Try to load language-specific labels first
            val languageSpecificPath = "$modelPath/labels/labels-$languageCode.txt"
            val fallbackPath = "$modelPath/labels/labels.txt"

            val inputStream: InputStream = try {
                context.assets.open(languageSpecificPath)
            } catch (e: IOException) {
                Log.w(TAG,"Language-specific labels not found ($languageSpecificPath), using default")
                context.assets.open(fallbackPath)
            }

            labels = inputStream.bufferedReader().useLines { lines ->
                lines.map { it.trim() }.filter { it.isNotEmpty() }.toList()
            }
            Log.i(TAG,"Loaded ${labels.size} labels")
        } catch (e: IOException) {
            Log.e(TAG,"Could not load labels.txt, using generic labels")
            labels = emptyList()
        } catch (e: Exception) {
            Log.e(TAG,"Error loading labels: ${e.message}")
            labels = emptyList()
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Create a fresh buffer for each classification to prevent data contamination
        val imageBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
            .apply { order(ByteOrder.nativeOrder()) }

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[pixel++]

                // Extract RGB values and normalize to [0, 1] or [-1, 1] depending on model
                val r = ((pixelValue shr 16) and 0xFF) / 255.0f
                val g = ((pixelValue shr 8) and 0xFF) / 255.0f
                val b = (pixelValue and 0xFF) / 255.0f

                // For models trained with ImageNet normalization, you might need:
                // val r = (((pixelValue shr 16) and 0xFF) - 123.68f) / 58.393f
                // val g = (((pixelValue shr 8) and 0xFF) - 116.78f) / 57.12f
                // val b = ((pixelValue and 0xFF) - 103.94f) / 57.375f

                imageBuffer.putFloat(r)
                imageBuffer.putFloat(g)
                imageBuffer.putFloat(b)
            }
        }

        imageBuffer.rewind() // Reset position for reading
        return imageBuffer
    }

    private fun processOutput(output: FloatArray): List<Prediction> {
        // Generate labels dynamically if not loaded
        if (labels.isEmpty()) {
            labels = (0 until output.size).map { "Class_$it" }
            Log.i(TAG,"Generated ${labels.size} generic labels for output size ${output.size}")
        }

        // Apply softmax to get probabilities
        val probabilities = softmax(output)

        // Create predictions with labels
        val predictions = mutableListOf<Prediction>()
        for (i in probabilities.indices) {
            val label = if (i < labels.size) labels[i] else "Class_$i"
            predictions.add(Prediction(label, probabilities[i]))
        }

        // Sort by confidence (descending) and return top predictions
        return predictions
            .sortedByDescending { it.confidence }
            .take(10) // Return top 10 predictions
    }

    private fun softmax(input: FloatArray): FloatArray {
        val maxVal = input.maxOrNull() ?: 0f
        val expValues = input.map { exp(it - maxVal) }
        val sumExp = expValues.sum()
        return expValues.map { (it / sumExp).toFloat() }.toFloatArray()
    }

    /**
     * Load TensorFlow Lite model file from assets
     */
    private fun loadModelFile(modelPath: String): ByteBuffer {
        val inputStream = context.assets.open(modelPath+"/model.tflite")
        val byteArray = inputStream.readBytes()
        inputStream.close()

        val modelBuffer = ByteBuffer.allocateDirect(byteArray.size)
        modelBuffer.order(ByteOrder.nativeOrder())
        modelBuffer.put(byteArray)
        modelBuffer.flip()

        return modelBuffer
    }

    /**
     * Center crop and resize image to target size
     * This ensures the image maintains aspect ratio during cropping before resizing
     */
    private fun centerCropAndResize(bitmap: Bitmap, targetSize: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        Log.i(TAG,"Original image size: ${originalWidth}x${originalHeight}")

        // Calculate the size of the square crop
        val cropSize = minOf(originalWidth, originalHeight)

        // Calculate the starting coordinates for center crop
        val startX = (originalWidth - cropSize) / 2
        val startY = (originalHeight - cropSize) / 2

        Log.i(TAG,"Center cropping to ${cropSize}x${cropSize} from (${startX}, ${startY})")

        // Create center-cropped square bitmap
        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            startX,
            startY,
            cropSize,
            cropSize
        )

        // Rotate 90 degrees clockwise
        val matrix = android.graphics.Matrix()
        matrix.postRotate(90f)
        val rotatedBitmap = Bitmap.createBitmap(
            croppedBitmap,
            0,
            0,
            croppedBitmap.width,
            croppedBitmap.height,
            matrix,
            true
        )

        Log.i(TAG,"Rotated image 90° clockwise")

        // Clean up cropped bitmap
        if (rotatedBitmap != croppedBitmap) {
            croppedBitmap.recycle()
        }

        // Resize the rotated bitmap to target size
        val resizedBitmap = Bitmap.createScaledBitmap(
            rotatedBitmap,
            targetSize,
            targetSize,
            true // Use bilinear filtering for better quality
        )

        // Clean up rotated bitmap
        if (resizedBitmap != rotatedBitmap) {
            rotatedBitmap.recycle()
        }

        Log.i(TAG,"Final processed image size: ${resizedBitmap.width}x${resizedBitmap.height}")

        return resizedBitmap
    }
}

/**
 * Platform-specific classifier creation function for ModelManager
 */
actual fun createPlatformClassifier(context: Any): ImageClassifier {
    val androidContext = context as Context
    return ImageClassifier(androidContext)
}