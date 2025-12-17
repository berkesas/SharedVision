package com.scinforma.sharedvision.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
@Composable
fun getHomeIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Home",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.0f, 20.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(22.0f)
            lineTo(12.0f, 3.0f)
            lineTo(2.0f, 12.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(10.0f)
            close()
        }
    }.build()
}

@Composable
fun getPersonIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Person",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 12.0f)
            curveTo(14.21f, 12.0f, 16.0f, 10.21f, 16.0f, 8.0f)
            curveTo(16.0f, 5.79f, 14.21f, 4.0f, 12.0f, 4.0f)
            curveTo(9.79f, 4.0f, 8.0f, 5.79f, 8.0f, 8.0f)
            curveTo(8.0f, 10.21f, 9.79f, 12.0f, 12.0f, 12.0f)
            close()
            moveTo(12.0f, 14.0f)
            curveTo(9.33f, 14.0f, 4.0f, 15.34f, 4.0f, 18.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(18.0f)
            curveTo(20.0f, 15.34f, 14.67f, 14.0f, 12.0f, 14.0f)
            close()
        }
    }.build()
}

@Composable
fun getStarIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Star",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 17.27f)
            lineTo(18.18f, 21.0f)
            lineTo(16.54f, 13.97f)
            lineTo(22.0f, 9.24f)
            lineTo(14.81f, 8.63f)
            lineTo(12.0f, 2.0f)
            lineTo(9.19f, 8.63f)
            lineTo(2.0f, 9.24f)
            lineTo(7.46f, 13.97f)
            lineTo(5.82f, 21.0f)
            lineTo(12.0f, 17.27f)
            close()
        }
    }.build()
}

@Composable
fun getEditIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Edit",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(3.0f, 17.25f)
            verticalLineTo(21.0f)
            horizontalLineTo(6.75f)
            lineTo(17.81f, 9.94f)
            lineTo(14.06f, 6.19f)
            lineTo(3.0f, 17.25f)
            close()
            moveTo(20.71f, 7.04f)
            curveTo(21.1f, 6.65f, 21.1f, 6.02f, 20.71f, 5.63f)
            lineTo(18.37f, 3.29f)
            curveTo(17.98f, 2.9f, 17.35f, 2.9f, 16.96f, 3.29f)
            lineTo(15.13f, 5.12f)
            lineTo(18.88f, 8.87f)
            lineTo(20.71f, 7.04f)
            close()
        }
    }.build()
}

@Composable
fun getModelIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Model",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 2.0f)
            lineTo(2.0f, 7.0f)
            verticalLineTo(17.0f)
            lineTo(12.0f, 22.0f)
            lineTo(22.0f, 17.0f)
            verticalLineTo(7.0f)
            lineTo(12.0f, 2.0f)
            close()
            moveTo(12.0f, 4.44f)
            lineTo(19.0f, 8.0f)
            verticalLineTo(9.0f)
            lineTo(12.0f, 12.56f)
            lineTo(5.0f, 9.0f)
            verticalLineTo(8.0f)
            lineTo(12.0f, 4.44f)
            close()
            moveTo(5.0f, 10.5f)
            lineTo(11.0f, 13.81f)
            verticalLineTo(19.94f)
            lineTo(5.0f, 16.5f)
            verticalLineTo(10.5f)
            close()
            moveTo(13.0f, 19.94f)
            verticalLineTo(13.81f)
            lineTo(19.0f, 10.5f)
            verticalLineTo(16.5f)
            lineTo(13.0f, 19.94f)
            close()
        }
    }.build()
}

@Composable
fun getCameraIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Camera",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.0f, 15.0f)
            curveTo(13.66f, 15.0f, 15.0f, 13.66f, 15.0f, 12.0f)
            curveTo(15.0f, 10.34f, 13.66f, 9.0f, 12.0f, 9.0f)
            curveTo(10.34f, 9.0f, 9.0f, 10.34f, 9.0f, 12.0f)
            curveTo(9.0f, 13.66f, 10.34f, 15.0f, 12.0f, 15.0f)
            close()
            moveTo(9.0f, 2.0f)
            lineTo(7.17f, 4.0f)
            horizontalLineTo(4.0f)
            curveTo(2.9f, 4.0f, 2.0f, 4.9f, 2.0f, 6.0f)
            verticalLineTo(18.0f)
            curveTo(2.0f, 19.1f, 2.9f, 20.0f, 4.0f, 20.0f)
            horizontalLineTo(20.0f)
            curveTo(21.1f, 20.0f, 22.0f, 19.1f, 22.0f, 18.0f)
            verticalLineTo(6.0f)
            curveTo(22.0f, 4.9f, 21.1f, 4.0f, 20.0f, 4.0f)
            horizontalLineTo(16.83f)
            lineTo(15.0f, 2.0f)
            horizontalLineTo(9.0f)
            close()
            moveTo(20.0f, 18.0f)
            horizontalLineTo(4.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(7.05f)
            lineTo(8.88f, 4.0f)
            horizontalLineTo(15.12f)
            lineTo(16.95f, 6.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(18.0f)
            close()
        }
    }.build()
}

@Composable
fun getImageIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Image",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.0f, 19.0f)
            verticalLineTo(5.0f)
            curveTo(21.0f, 3.9f, 20.1f, 3.0f, 19.0f, 3.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
            verticalLineTo(19.0f)
            curveTo(3.0f, 20.1f, 3.9f, 21.0f, 5.0f, 21.0f)
            horizontalLineTo(19.0f)
            curveTo(20.1f, 21.0f, 21.0f, 20.1f, 21.0f, 19.0f)
            close()
            moveTo(8.5f, 13.5f)
            lineTo(11.0f, 16.51f)
            lineTo(14.5f, 12.0f)
            lineTo(19.0f, 18.0f)
            horizontalLineTo(5.0f)
            lineTo(8.5f, 13.5f)
            close()
        }
    }.build()
}

@Composable
fun getTextIcon(): ImageVector {
    return ImageVector.Builder(
        name = "TextFields",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(2.5f, 4.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(12.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(18.5f)
            verticalLineTo(4.0f)
            horizontalLineTo(2.5f)
            close()
            moveTo(21.0f, 9.0f)
            horizontalLineTo(12.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(15.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(9.0f)
            close()
        }
    }.build()
}

@Composable
fun getSettingsIcon(): ImageVector {
    return ImageVector.Builder(
        name = "Settings",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.14f, 12.94f)
            curveTo(19.18f, 12.64f, 19.2f, 12.33f, 19.2f, 12.0f)
            curveTo(19.2f, 11.68f, 19.18f, 11.36f, 19.13f, 11.06f)
            lineTo(21.16f, 9.48f)
            curveTo(21.34f, 9.34f, 21.39f, 9.07f, 21.28f, 8.87f)
            lineTo(19.36f, 5.55f)
            curveTo(19.24f, 5.33f, 18.99f, 5.26f, 18.77f, 5.33f)
            lineTo(16.38f, 6.29f)
            curveTo(15.88f, 5.91f, 15.35f, 5.59f, 14.76f, 5.35f)
            lineTo(14.4f, 2.81f)
            curveTo(14.36f, 2.57f, 14.16f, 2.4f, 13.92f, 2.4f)
            horizontalLineTo(10.08f)
            curveTo(9.84f, 2.4f, 9.65f, 2.57f, 9.61f, 2.81f)
            lineTo(9.25f, 5.35f)
            curveTo(8.66f, 5.59f, 8.12f, 5.92f, 7.63f, 6.29f)
            lineTo(5.24f, 5.33f)
            curveTo(5.02f, 5.25f, 4.77f, 5.33f, 4.65f, 5.55f)
            lineTo(2.74f, 8.87f)
            curveTo(2.62f, 9.08f, 2.66f, 9.34f, 2.86f, 9.48f)
            lineTo(4.89f, 11.06f)
            curveTo(4.84f, 11.36f, 4.8f, 11.69f, 4.8f, 12.0f)
            curveTo(4.8f, 12.31f, 4.82f, 12.64f, 4.87f, 12.94f)
            lineTo(2.84f, 14.52f)
            curveTo(2.66f, 14.66f, 2.61f, 14.93f, 2.72f, 15.13f)
            lineTo(4.64f, 18.45f)
            curveTo(4.76f, 18.67f, 5.01f, 18.74f, 5.23f, 18.67f)
            lineTo(7.62f, 17.71f)
            curveTo(8.12f, 18.09f, 8.65f, 18.41f, 9.24f, 18.65f)
            lineTo(9.6f, 21.19f)
            curveTo(9.64f, 21.43f, 9.84f, 21.6f, 10.08f, 21.6f)
            horizontalLineTo(13.92f)
            curveTo(14.16f, 21.6f, 14.35f, 21.43f, 14.39f, 21.19f)
            lineTo(14.75f, 18.65f)
            curveTo(15.34f, 18.41f, 15.88f, 18.09f, 16.37f, 17.71f)
            lineTo(18.76f, 18.67f)
            curveTo(18.98f, 18.75f, 19.23f, 18.67f, 19.35f, 18.45f)
            lineTo(21.26f, 15.13f)
            curveTo(21.38f, 14.91f, 21.34f, 14.66f, 21.14f, 14.52f)
            lineTo(19.14f, 12.94f)
            close()
            moveTo(12.0f, 15.6f)
            curveTo(10.02f, 15.6f, 8.4f, 13.98f, 8.4f, 12.0f)
            curveTo(8.4f, 10.02f, 10.02f, 8.4f, 12.0f, 8.4f)
            curveTo(13.98f, 8.4f, 15.6f, 10.02f, 15.6f, 12.0f)
            curveTo(15.6f, 13.98f, 13.98f, 15.6f, 12.0f, 15.6f)
            close()
        }
    }.build()
}