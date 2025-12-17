package com.example.sharedvision.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.scinforma.sharedvision.R

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = colorResource(id = R.color.primary),
            onPrimary = colorResource(id = R.color.onPrimary),
            secondary = colorResource(id = R.color.secondary),
            onSecondary = Color.White,
            background = colorResource(id = R.color.background),
            onBackground = Color.White,
            surface = colorResource(id = R.color.background),
            onSurface = Color.White,
        )
    } else {
        lightColorScheme(
            primary = colorResource(id = R.color.primary),
            onPrimary = colorResource(id = R.color.onPrimary),
            secondary = colorResource(id = R.color.secondary),
            onSecondary = Color.Black,
            background = colorResource(id = R.color.background),
            onBackground = Color.Black,
            surface = colorResource(id = R.color.background),
            onSurface = Color.Black,
        )
    }

    MaterialTheme(
        colorScheme = colors,
//        typography = Typography, // Your Typography.kt
//        shapes = Shapes,         // Your Shapes.kt
        content = content
    )
}
