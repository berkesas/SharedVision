// File: composeApp/src/commonMain/kotlin/com/scinforma/sharedvision/AppConfig.kt
package com.scinforma.sharedvision

object AppConfig {
    // Debug flags
    const val DEBUG_MODE = true
    const val SAVE_PROCESSED_IMAGES = false
    const val SAVE_RAW_IMAGES = false
    const val VERBOSE_LOGGING = true

    // App settings
    const val DEFAULT_MODEL_PATH = "manat"
    const val IMAGE_INPUT_SIZE = 224

    // Any other app-wide constants
}