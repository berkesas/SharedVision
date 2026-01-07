package com.scinforma.sharedvision.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Common interface for camera image data
 */
expect class CameraImage {
    fun close()
    fun toBitmap(): ImageBitmap
    fun toJpegByteArray(quality: Int = 100): ByteArray
}

/**
 * Camera preview composable
 */
@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptured: (CameraImage) -> Unit
)