package com.scinforma.sharedvision.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "CameraPreview"

/**
 * Android implementation of CameraImage wrapping ImageProxy
 */
actual class CameraImage(private val imageProxy: ImageProxy) {

    actual fun close() {
        imageProxy.close()
    }

    actual fun toBitmap(): ImageBitmap {
        return convertImageProxyToBitmap(imageProxy).asImageBitmap()
    }

    private fun convertImageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        return try {
            when (imageProxy.format) {
                ImageFormat.JPEG -> {
                    val buffer: ByteBuffer = imageProxy.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
                PixelFormat.RGBA_8888 -> {
                    // Direct RGBA conversion - fast and reliable
                    val buffer = imageProxy.planes[0].buffer
                    buffer.rewind()
                    val bitmap = Bitmap.createBitmap(
                        imageProxy.width,
                        imageProxy.height,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.copyPixelsFromBuffer(buffer)
                    bitmap
                }
                ImageFormat.YUV_420_888 -> {
//                    Log.d(TAG, "Using YUV conversion")
                    convertYuv420ToBitmapRobust(imageProxy)  // Use the robust version
                }
                else -> {
                    Log.w(TAG, "Unsupported image format: ${imageProxy.format}, creating placeholder")
                    Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting image to bitmap", e)
            Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        }
    }

    private fun convertYuv420ToBitmap(imageProxy: ImageProxy): Bitmap {
        return try {
            val planes = imageProxy.planes
            val yBuffer = planes[0].buffer
            val uBuffer = planes[1].buffer
            val vBuffer = planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            // Copy Y plane
            yBuffer.rewind()
            yBuffer.get(nv21, 0, ySize)

            // Check if U and V planes are packed or need interleaving
            val pixelStride = planes[2].pixelStride

            if (pixelStride == 1) {
                // U and V are packed
                vBuffer.rewind()
                vBuffer.get(nv21, ySize, vSize)
                uBuffer.rewind()
                uBuffer.get(nv21, ySize + vSize, uSize)
            } else {
                // U and V need manual interleaving for NV21 format
                val width = imageProxy.width
                val height = imageProxy.height
                val uvWidth = width / 2
                val uvHeight = height / 2
                val rowStride = planes[2].rowStride

                vBuffer.rewind()
                uBuffer.rewind()

                var pos = ySize
                for (row in 0 until uvHeight) {
                    for (col in 0 until uvWidth) {
                        val vuPos = row * rowStride + col * pixelStride
                        nv21[pos++] = vBuffer.get(vuPos)
                        nv21[pos++] = uBuffer.get(vuPos)
                    }
                }
            }

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val outputStream = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 90, outputStream)
            val imageBytes = outputStream.toByteArray()

            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "Error in YUV conversion", e)
            Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
        }
    }

    private fun convertYuv420ToBitmapRobust(imageProxy: ImageProxy): Bitmap {
        val width = imageProxy.width
        val height = imageProxy.height

        // Create output bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        try {
            // Use libyuv or manual RGB conversion
            val yuvBytes = ByteArray(width * height * 3 / 2)

            // Extract Y plane
            val yBuffer = imageProxy.planes[0].buffer
            val yBytes = ByteArray(yBuffer.remaining())
            yBuffer.get(yBytes)
            System.arraycopy(yBytes, 0, yuvBytes, 0, yBytes.size)

            // Extract and interleave UV planes
            val uBuffer = imageProxy.planes[1].buffer
            val vBuffer = imageProxy.planes[2].buffer
            val uvPixelStride = imageProxy.planes[1].pixelStride

            var pos = yBytes.size
            for (i in 0 until uBuffer.remaining() step uvPixelStride) {
                yuvBytes[pos++] = vBuffer.get(i)
                yuvBytes[pos++] = uBuffer.get(i)
            }

            // Convert to RGB
            val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
            val stream = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, stream)

            val jpegData = stream.toByteArray()
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size) ?: bitmap

        } catch (e: Exception) {
            Log.e(TAG, "Robust YUV conversion failed", e)
            bitmap.eraseColor(android.graphics.Color.BLUE)
            return bitmap
        }
    }

    /**
     * Convert image to raw JPEG bytes without any processing
     */
    actual fun toJpegByteArray(quality: Int): ByteArray {
        return when (imageProxy.format) {
            ImageFormat.JPEG -> {
                // Already JPEG - return raw bytes directly
                val buffer = imageProxy.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.rewind()
                buffer.get(bytes)
                bytes
            }
            ImageFormat.YUV_420_888 -> {
                // Convert YUV to JPEG
                convertYuvToJpegBytes(quality)
            }
            PixelFormat.RGBA_8888 -> {
                // Convert RGBA to JPEG
                convertRgbaToJpegBytes(quality)
            }
            else -> {
                Log.w(TAG, "Unsupported format for JPEG conversion: ${imageProxy.format}")
                ByteArray(0)
            }
        }
    }

    private fun convertYuvToJpegBytes(quality: Int): ByteArray {
        val width = imageProxy.width
        val height = imageProxy.height

        val yuvBytes = ByteArray(width * height * 3 / 2)

        // Extract Y plane
        val yBuffer = imageProxy.planes[0].buffer
        val yBytes = ByteArray(yBuffer.remaining())
        yBuffer.rewind()
        yBuffer.get(yBytes)
        System.arraycopy(yBytes, 0, yuvBytes, 0, yBytes.size)

        // Extract and interleave UV planes
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer
        val uvPixelStride = imageProxy.planes[1].pixelStride

        uBuffer.rewind()
        vBuffer.rewind()

        var pos = yBytes.size
        for (i in 0 until uBuffer.remaining() step uvPixelStride) {
            yuvBytes[pos++] = vBuffer.get(i)
            yuvBytes[pos++] = uBuffer.get(i)
        }

        // Convert to JPEG
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), quality, outputStream)

        return outputStream.toByteArray()
    }

    private fun convertRgbaToJpegBytes(quality: Int): ByteArray {
        val buffer = imageProxy.planes[0].buffer
        buffer.rewind()
        val bitmap = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        bitmap.recycle()

        return outputStream.toByteArray()
    }
}

/**
 * Android Camera Preview implementation with continuous frame capture
 */
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    onImageCaptured: (CameraImage) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Use AtomicLong for thread-safe timestamp tracking
    val lastCaptureTime = remember { AtomicLong(0L) }
    val captureIntervalMs = 200L // Capture one frame per second

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "Initializing camera with frame throttling (${captureIntervalMs}ms interval)")
    }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                Log.d(TAG, "Camera provider obtained")

                cameraProvider.unbindAll()

                val preview = Preview.Builder()
                    .build()
                    .also { preview ->
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    // Don't force RGBA - let camera use default format (usually YUV)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
//                            Log.d(TAG, "=== ANALYZER CALLED === Format: ${imageProxy.format}, Size: ${imageProxy.width}x${imageProxy.height}")

                            try {
                                val currentTime = System.currentTimeMillis()
                                val lastTime = lastCaptureTime.get()

//                                Log.d(TAG, "Time check: current=$currentTime, last=$lastTime, diff=${currentTime - lastTime}ms")

                                // Throttle: only process frames at specified interval
                                if (currentTime - lastTime >= captureIntervalMs) {
//                                    Log.d(TAG, "Accepting frame - calling onImageCaptured")
                                    lastCaptureTime.set(currentTime)

                                    val cameraImage = CameraImage(imageProxy)
                                    onImageCaptured(cameraImage)
                                    // Note: onImageCaptured is responsible for closing the imageProxy
                                } else {
//                                    Log.d(TAG, "Skipping frame")
                                    imageProxy.close()  // MUST close immediately
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing image", e)
                                e.printStackTrace()
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val availableCameras = cameraProvider.availableCameraInfos
                Log.d(TAG, "Available cameras: ${availableCameras.size}")

                if (availableCameras.isEmpty()) {
                    Log.e(TAG, "No cameras available")
                    return@addListener
                }

                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                Log.d(TAG, "Camera bound successfully: ${camera.cameraInfo}")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind camera use cases", e)
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            Log.d(TAG, "Disposing camera resources")
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (e: Exception) {
                Log.w(TAG, "Error unbinding camera", e)
            }
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            Log.d(TAG, "AndroidView update called")
        }
    )
}