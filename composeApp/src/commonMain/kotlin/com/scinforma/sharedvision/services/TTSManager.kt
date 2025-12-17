package com.scinforma.sharedvision.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.scinforma.sharedvision.data.ILanguagePreferences
import com.scinforma.sharedvision.utils.Logger
import java.util.*

/**
 * Text-to-Speech Manager
 * Handles text-to-speech functionality across the app
 */
object TTSManager {
    private const val TAG = "TTSManager"

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val pendingTexts = mutableListOf<String>()
    private var languagePreferences: ILanguagePreferences? = null

    /**
     * Initialize TTS engine
     */
    fun initialize(context: Context, preferences: ILanguagePreferences? = null) {
        // Check available engines BEFORE initialization
        val tempTts = TextToSpeech(context, null)
        tempTts.engines.forEach { engine ->
            Logger.i(TAG, "Found engine: ${engine.label} - ${engine.name}")
        }
        tempTts.shutdown()

        if (tts != null) {
            Logger.d(TAG, "TTS already initialized")
            return
        }

        languagePreferences = preferences

        Logger.d(TAG, "Initializing TTS")
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                isInitialized = true
                Logger.i(TAG, "TTS initialized successfully")

                // Speak any pending texts
                if (pendingTexts.isNotEmpty()) {
                    Logger.d(TAG, "Speaking ${pendingTexts.size} pending texts")
                    pendingTexts.forEach { text ->
                        speak(text)
                    }
                    pendingTexts.clear()
                }
            } else {
                Logger.e(TAG, "TTS initialization failed with status: $status")
                isInitialized = false
            }
        }

        // Set utterance progress listener for debugging
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Logger.d(TAG, "TTS started speaking: $utteranceId")
            }

            override fun onDone(utteranceId: String?) {
                Logger.d(TAG, "TTS finished speaking: $utteranceId")
            }

            override fun onError(utteranceId: String?) {
                Logger.e(TAG, "TTS error: $utteranceId")
            }
        })
    }

    /**
     * Speak the given text in the current voice language
     */
    fun speak(text: String) {
        if (text.isBlank()) {
            Logger.d(TAG, "Skipping empty text")
            return
        }

        if (!isInitialized || tts == null) {
            Logger.w(TAG, "TTS not initialized, queueing text: ${text.take(50)}...")
            pendingTexts.add(text)
            return
        }

        // Get the voice language from preferences
        val voiceLanguageCode = languagePreferences?.getVoiceLanguage()
        val targetLocale = if (!voiceLanguageCode.isNullOrBlank()) {
            parseLocale(voiceLanguageCode)
        } else {
            Locale.getDefault()
        }

        Logger.d(TAG, "Attempting to set language to: $targetLocale")

        // Try to set the target language
        val langResult = tts?.setLanguage(targetLocale)
        when (langResult) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                Logger.w(TAG, "Language $targetLocale not supported, falling back to system default")
                // Fall back to system default
                val fallbackResult = tts?.setLanguage(Locale.getDefault())
                if (fallbackResult == TextToSpeech.LANG_MISSING_DATA ||
                    fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Logger.e(TAG, "Even system default language not supported")
                }
            }
            else -> {
                Logger.i(TAG, "Language successfully set to: $targetLocale")
            }
        }

        Logger.d(TAG, "Speaking: ${text.take(50)}...")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
    }

    /**
     * Parse language code string to Locale
     * Supports formats like "en", "en-US", "en_US"
     */
    private fun parseLocale(languageCode: String): Locale {
        return try {
            val parts = languageCode.replace("-", "_").split("_")
            when (parts.size) {
                1 -> Locale(parts[0])
                2 -> Locale(parts[0], parts[1])
                3 -> Locale(parts[0], parts[1], parts[2])
                else -> {
                    Logger.w(TAG, "Invalid language code format: $languageCode, using default")
                    Locale.getDefault()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error parsing locale: $languageCode", e)
            Locale.getDefault()
        }
    }

    /**
     * Stop speaking
     */
    fun stop() {
        if (isInitialized && tts != null) {
            Logger.d(TAG, "Stopping TTS")
            tts?.stop()
        }
    }

    /**
     * Check if TTS is currently speaking
     */
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    /**
     * Set TTS language
     */
    fun setLanguage(locale: Locale): Boolean {
        if (!isInitialized || tts == null) {
            Logger.w(TAG, "Cannot set language, TTS not initialized")
            return false
        }

        val result = tts?.setLanguage(locale)
        return when (result) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                Logger.e(TAG, "Language not supported: $locale")
                false
            }
            else -> {
                Logger.i(TAG, "Language set to: $locale")
                true
            }
        }
    }

    /**
     * Shutdown TTS engine
     */
    fun shutdown() {
        Logger.d(TAG, "Shutting down TTS")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        pendingTexts.clear()
        languagePreferences = null
    }
}