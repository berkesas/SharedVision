package com.scinforma.sharedvision.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun getAddIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Add",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.0f, 13.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(13.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(5.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(13.0f)
            close()
        }
    }.build()
}

@Composable
fun getStorageIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Storage",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4.0f, 6.0f)
            horizontalLineTo(2.0f)
            verticalLineTo(20.0f)
            curveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(4.0f)
            verticalLineTo(6.0f)
            close()
            moveTo(20.0f, 2.0f)
            horizontalLineTo(8.0f)
            curveTo(6.9f, 2.0f, 6.0f, 2.9f, 6.0f, 4.0f)
            verticalLineTo(16.0f)
            curveTo(6.0f, 17.1f, 6.9f, 18.0f, 8.0f, 18.0f)
            horizontalLineTo(20.0f)
            curveTo(21.1f, 18.0f, 22.0f, 17.1f, 22.0f, 16.0f)
            verticalLineTo(4.0f)
            curveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f)
            close()
            moveTo(20.0f, 16.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(4.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(16.0f)
            close()
        }
    }.build()
}

@Composable
fun getDownloadIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Download",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5.0f, 20.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(20.0f)
            close()
            moveTo(13.0f, 17.17f)
            verticalLineTo(4.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(17.17f)
            lineTo(5.5f, 11.67f)
            lineTo(6.91f, 10.26f)
            lineTo(12.0f, 15.35f)
            lineTo(17.09f, 10.26f)
            lineTo(18.5f, 11.67f)
            lineTo(13.0f, 17.17f)
            close()
        }
    }.build()
}

@Composable
fun getCheckIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Check",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(9.0f, 16.17f)
            lineTo(4.83f, 12.0f)
            lineTo(3.41f, 13.41f)
            lineTo(9.0f, 19.0f)
            lineTo(21.0f, 7.0f)
            lineTo(19.59f, 5.59f)
            lineTo(9.0f, 16.17f)
            close()
        }
    }.build()
}

@Composable
fun getCloudIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Cloud",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.35f, 10.04f)
            curveTo(18.67f, 6.59f, 15.64f, 4.0f, 12.0f, 4.0f)
            curveTo(9.11f, 4.0f, 6.6f, 5.64f, 5.35f, 8.04f)
            curveTo(2.34f, 8.36f, 0.0f, 10.91f, 0.0f, 14.0f)
            curveTo(0.0f, 17.31f, 2.69f, 20.0f, 6.0f, 20.0f)
            horizontalLineTo(19.0f)
            curveTo(21.76f, 20.0f, 24.0f, 17.76f, 24.0f, 15.0f)
            curveTo(24.0f, 12.36f, 21.95f, 10.22f, 19.35f, 10.04f)
            close()
            moveTo(19.0f, 18.0f)
            horizontalLineTo(6.0f)
            curveTo(3.79f, 18.0f, 2.0f, 16.21f, 2.0f, 14.0f)
            curveTo(2.0f, 11.95f, 3.53f, 10.24f, 5.56f, 10.03f)
            lineTo(6.63f, 9.92f)
            lineTo(7.13f, 8.97f)
            curveTo(8.08f, 7.14f, 9.94f, 6.0f, 12.0f, 6.0f)
            curveTo(14.62f, 6.0f, 16.88f, 7.86f, 17.39f, 10.43f)
            lineTo(17.69f, 11.93f)
            lineTo(19.22f, 12.04f)
            curveTo(20.78f, 12.14f, 22.0f, 13.45f, 22.0f, 15.0f)
            curveTo(22.0f, 16.65f, 20.65f, 18.0f, 19.0f, 18.0f)
            close()
        }
    }.build()
}

@Composable
fun getDeleteIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Delete",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.0f, 19.0f)
            curveTo(6.0f, 20.1f, 6.9f, 21.0f, 8.0f, 21.0f)
            horizontalLineTo(16.0f)
            curveTo(17.1f, 21.0f, 18.0f, 20.1f, 18.0f, 19.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(19.0f)
            close()
            moveTo(19.0f, 4.0f)
            horizontalLineTo(15.5f)
            lineTo(14.5f, 3.0f)
            horizontalLineTo(9.5f)
            lineTo(8.5f, 4.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(4.0f)
            close()
        }
    }.build()
}

@Composable
fun getInfoIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Info",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 2.0f)
            curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
            curveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f)
            curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
            curveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f)
            close()
            moveTo(13.0f, 17.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(17.0f)
            close()
            moveTo(13.0f, 9.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(9.0f)
            close()
        }
    }.build()
}

@Composable
fun getFileSizeIcon(): ImageVector {
    return ImageVector.Builder(
        name = "FileSize",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.0f, 2.0f)
            horizontalLineTo(6.0f)
            curveTo(4.9f, 2.0f, 4.01f, 2.9f, 4.01f, 4.0f)
            lineTo(4.0f, 20.0f)
            curveTo(4.0f, 21.1f, 4.89f, 22.0f, 5.99f, 22.0f)
            horizontalLineTo(18.0f)
            curveTo(19.1f, 22.0f, 20.0f, 21.1f, 20.0f, 20.0f)
            verticalLineTo(8.0f)
            lineTo(14.0f, 2.0f)
            close()
            moveTo(18.0f, 20.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(4.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(20.0f)
            close()
        }
    }.build()
}

@Composable
fun getAccuracyIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Accuracy",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 2.0f)
            lineTo(13.09f, 8.26f)
            lineTo(22.0f, 9.27f)
            lineTo(17.0f, 14.14f)
            lineTo(18.18f, 21.02f)
            lineTo(12.0f, 17.77f)
            lineTo(5.82f, 21.02f)
            lineTo(7.0f, 14.14f)
            lineTo(2.0f, 9.27f)
            lineTo(10.91f, 8.26f)
            lineTo(12.0f, 2.0f)
            close()
        }
    }.build()
}

@Composable
fun getVersionIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Version",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 2.0f)
            lineTo(2.0f, 7.0f)
            lineTo(12.0f, 12.0f)
            lineTo(22.0f, 7.0f)
            lineTo(12.0f, 2.0f)
            close()
            moveTo(2.0f, 17.0f)
            lineTo(12.0f, 22.0f)
            lineTo(22.0f, 17.0f)
            lineTo(12.0f, 12.0f)
            lineTo(2.0f, 17.0f)
            close()
            moveTo(2.0f, 12.0f)
            lineTo(12.0f, 17.0f)
            lineTo(22.0f, 12.0f)
            lineTo(12.0f, 7.0f)
            lineTo(2.0f, 12.0f)
            close()
        }
    }.build()
}