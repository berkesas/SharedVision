package com.scinforma.sharedvision.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.speech.tts.TextToSpeech
import android.util.Log
import com.scinforma.sharedvision.AppConfig
import java.util.Locale

private var textToSpeech: TextToSpeech? = null
private var isInitialized = false
private val pendingText = mutableListOf<String>()

//actual fun speakText(context: Any, text: String) {
//    val androidContext = context as Context
//
//    if (textToSpeech == null) {
//        textToSpeech = TextToSpeech(androidContext) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                // Set language to Russian
//                val locale = Locale("ru", "RU")  // Russian (Russia)
//                val result = textToSpeech?.setLanguage(locale)
//
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.w("TTS", "Russian language not available, using default")
//                } else {
//                    Log.d("TTS", "Successfully set language to Russian")
//                }
//
//                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//            }
//        }
//    } else {
//        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//    }
//}

//actual fun speakText(context: Any, text: String) {
//    val androidContext = context as Context
//
//    if (textToSpeech == null) {
//        textToSpeech = TextToSpeech(androidContext) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                Log.d("TTS", "TTS initialized successfully")
//                // Don't set language - use system default
//                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//            } else {
//                Log.e("TTS", "TTS initialization failed")
//            }
//        }
//    } else {
//        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//    }
//}

//actual fun speakText(context: Any, text: String) {
//    val androidContext = context as Context
//
//    Log.d("TTS", "speakText called: '$text', isInitialized: $isInitialized")
//
//    if (textToSpeech == null) {
//        pendingText.add(text)
//        Log.d("TTS", "Initializing TTS, text added to pending queue")
//        textToSpeech = TextToSpeech(
//            androidContext,
//            { status ->
//                Log.d("TTS", "Init callback, status: $status")
//                if (status == TextToSpeech.SUCCESS) {
//                    isInitialized = true
//                    Log.d("TTS", "Speaking ${pendingText.size} pending items")
//                    pendingText.forEach {
//                        textToSpeech?.speak(it, TextToSpeech.QUEUE_ADD, null, null)
//                    }
//                    pendingText.clear()
//                } else {
//                    Log.e("TTS", "Init failed")
//                }
//            },
//            "com.github.olga_yakovleva.rhvoice.android"
//        )
//    } else if (isInitialized) {
//        Log.d("TTS", "Speaking immediately")
//        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//    } else {
//        Log.d("TTS", "TTS initializing, adding to queue")
//        pendingText.add(text)
//    }
//}
actual fun checkCameraPermission(context: Any): Boolean {
    val androidContext = context as Context
    return ContextCompat.checkSelfPermission(
        androidContext,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
actual fun RequestCameraPermission(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onPermissionResult
    )

    return {
        launcher.launch(Manifest.permission.CAMERA)
    }
}

@Composable
actual fun getLocalContext(): Any {
    return LocalContext.current
}

actual suspend fun saveImageForTesting(context: Any, imageBitmap: ImageBitmap, filename: String): String? {
    if (!AppConfig.SAVE_RAW_IMAGES) {
        return null
    }

    return withContext(Dispatchers.IO) {
        try {
            val androidContext = context as android.content.Context
            val testDir = File(androidContext.getExternalFilesDir(null), "test_images")
            if (!testDir.exists()) {
                testDir.mkdirs()
            }

            val imageFile = File(testDir, filename)
            val androidBitmap = imageBitmap.asAndroidBitmap()

            FileOutputStream(imageFile).use { out ->
                androidBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }

            println("DEBUG: Image saved to: ${imageFile.absolutePath}")
            imageFile.absolutePath
        } catch (e: Exception) {
            println("ERROR: Failed to save image: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

//actual fun shutdownTTS() {
//    textToSpeech?.stop()
//    textToSpeech?.shutdown()
//    textToSpeech = null
//}