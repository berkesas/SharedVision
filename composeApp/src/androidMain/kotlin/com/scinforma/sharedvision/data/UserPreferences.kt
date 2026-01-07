package com.scinforma.sharedvision.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) : IUserPreferences {

    companion object {
        private const val PREFS_NAME = "language_preferences"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val KEY_AUTO_READ_INTERVAL = "auto_read_interval"
        private const val KEY_TTS_ENABLED = "tts_enabled"
        private const val KEY_CURRENT_MODEL = "current_model"
        private const val KEY_LABEL_LANGUAGE = "label_language"
        private const val KEY_VOICE_LANGUAGE = "voice_language"

        private const val KEY_ACCESS_CODE = "access_code"


        private const val KEY_PREDICTION_THRESHOLD = "prediction_threshold"

        private const val DEFAULT_AUTO_READ_INTERVAL = 5000L
        private const val DEFAULT_TTS_ENABLED = false
        private const val DEFAULT_MODEL = "manat"
        private const val DEFAULT_LABEL_LANGUAGE = "en"
        private const val DEFAULT_VOICE_LANGUAGE = "en-US"
        private const val DEFAULT_PREDICTION_THRESHOLD = 20L

        private const val DEFAULT_ACCESS_CODE = ""

    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedLanguageFlow = MutableStateFlow(getSelectedLanguage())
    override val selectedLanguageFlow: StateFlow<String?> = _selectedLanguageFlow.asStateFlow()

    private val _autoReadIntervalFlow = MutableStateFlow(getAutoReadInterval())
    override val autoReadIntervalFlow: StateFlow<Long> = _autoReadIntervalFlow.asStateFlow()

    private val _ttsEnabledFlow = MutableStateFlow(getTtsEnabled())
    override val ttsEnabledFlow: StateFlow<Boolean> = _ttsEnabledFlow.asStateFlow()

    private val _currentModelFlow = MutableStateFlow(getCurrentModel())
    override val currentModelFlow: StateFlow<String> = _currentModelFlow.asStateFlow()

    private val _labelLanguageFlow = MutableStateFlow(getLabelLanguage())
    override val labelLanguageFlow: StateFlow<String> = _labelLanguageFlow.asStateFlow()

    private val _voiceLanguageFlow = MutableStateFlow(getVoiceLanguage())
    override val voiceLanguageFlow: StateFlow<String> = _voiceLanguageFlow.asStateFlow()

    private val _predictionThresholdFlow = MutableStateFlow(getPredictionThreshold())
    override val predictionThresholdFlow: StateFlow<Long> = _predictionThresholdFlow.asStateFlow()

    private val _accessCodeFlow = MutableStateFlow(getAccessCode())
    override val accessCodeFlow: StateFlow<String> = _accessCodeFlow.asStateFlow()

    override fun getSelectedLanguage(): String? {
        return sharedPreferences.getString(KEY_SELECTED_LANGUAGE, null)
    }

    override fun setSelectedLanguage(languageCode: String?) {
        sharedPreferences.edit().apply {
            if (languageCode == null) {
                remove(KEY_SELECTED_LANGUAGE)
            } else {
                putString(KEY_SELECTED_LANGUAGE, languageCode)
            }
            apply()
        }
        _selectedLanguageFlow.value = languageCode
    }

    override fun getAutoReadInterval(): Long {
        return sharedPreferences.getLong(KEY_AUTO_READ_INTERVAL, DEFAULT_AUTO_READ_INTERVAL)
    }

    override fun setAutoReadInterval(intervalMs: Long) {
        sharedPreferences.edit().apply {
            putLong(KEY_AUTO_READ_INTERVAL, intervalMs)
            apply()
        }
        _autoReadIntervalFlow.value = intervalMs
    }

    override fun getTtsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_TTS_ENABLED, DEFAULT_TTS_ENABLED)
    }

    override fun setTtsEnabled(enabled: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_TTS_ENABLED, enabled)
            apply()
        }
        _ttsEnabledFlow.value = enabled
    }

    override fun getCurrentModel(): String {
        return sharedPreferences.getString(KEY_CURRENT_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    override fun setCurrentModel(modelPath: String) {
        sharedPreferences.edit().apply {
            putString(KEY_CURRENT_MODEL, modelPath)
            apply()
        }
        _currentModelFlow.value = modelPath
    }

    override fun getLabelLanguage(): String {
        return sharedPreferences.getString(KEY_LABEL_LANGUAGE, DEFAULT_LABEL_LANGUAGE) ?: DEFAULT_LABEL_LANGUAGE
    }

    override fun setLabelLanguage(languageCode: String) {
        sharedPreferences.edit().apply {
            putString(KEY_LABEL_LANGUAGE, languageCode)
            apply()
        }
        _labelLanguageFlow.value = languageCode
    }

    override fun getVoiceLanguage(): String {
        return sharedPreferences.getString(KEY_VOICE_LANGUAGE, DEFAULT_VOICE_LANGUAGE) ?: DEFAULT_VOICE_LANGUAGE
    }

    override fun setVoiceLanguage(languageCode: String) {
        sharedPreferences.edit().apply {
            putString(KEY_VOICE_LANGUAGE, languageCode)
            apply()
        }
        _voiceLanguageFlow.value = languageCode
    }

    override fun getPredictionThreshold(): Long {
        return sharedPreferences.getLong(KEY_PREDICTION_THRESHOLD, DEFAULT_PREDICTION_THRESHOLD)
    }

    override fun setPredictionThreshold(threshold: Long) {
        sharedPreferences.edit().apply {
            putLong(KEY_PREDICTION_THRESHOLD, threshold)
            apply()
        }
        _predictionThresholdFlow.value = threshold
    }

    override fun getAccessCode(): String {
        return sharedPreferences.getString(KEY_ACCESS_CODE, DEFAULT_ACCESS_CODE) ?: DEFAULT_VOICE_LANGUAGE
    }

    override fun setAccessCode(accessCode: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_CODE, accessCode)
            apply()
        }
        _accessCodeFlow.value = accessCode
    }
}