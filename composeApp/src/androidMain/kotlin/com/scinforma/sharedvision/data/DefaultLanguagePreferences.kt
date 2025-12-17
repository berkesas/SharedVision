package com.scinforma.sharedvision.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultLanguagePreferences : ILanguagePreferences {
    private val _selectedLanguageFlow = MutableStateFlow<String?>(null)
    override val selectedLanguageFlow: StateFlow<String?> = _selectedLanguageFlow.asStateFlow()

    private val _autoReadIntervalFlow = MutableStateFlow(2000L)
    override val autoReadIntervalFlow: StateFlow<Long> = _autoReadIntervalFlow.asStateFlow()

    private val _ttsEnabledFlow = MutableStateFlow(true)
    override val ttsEnabledFlow: StateFlow<Boolean> = _ttsEnabledFlow.asStateFlow()

    private val _currentModelFlow = MutableStateFlow("manat")
    override val currentModelFlow: StateFlow<String> = _currentModelFlow.asStateFlow()

    private val _labelLanguageFlow = MutableStateFlow("en")
    override val labelLanguageFlow: StateFlow<String> = _labelLanguageFlow.asStateFlow()

    private val _voiceLanguageFlow = MutableStateFlow("en-US")
    override val voiceLanguageFlow: StateFlow<String> = _voiceLanguageFlow.asStateFlow()

    override fun getSelectedLanguage(): String? = null

    override fun setSelectedLanguage(languageCode: String?) {
        _selectedLanguageFlow.value = languageCode
    }

    override fun getAutoReadInterval(): Long = 2000L

    override fun setAutoReadInterval(intervalMs: Long) {
        _autoReadIntervalFlow.value = intervalMs
    }

    override fun getTtsEnabled(): Boolean = false

    override fun setTtsEnabled(enabled: Boolean) {
        _ttsEnabledFlow.value = enabled
    }

    override fun getCurrentModel(): String = "manat"

    override fun setCurrentModel(modelPath: String) {
        _currentModelFlow.value = modelPath
    }

    override fun getLabelLanguage(): String = "en"

    override fun setLabelLanguage(languageCode: String) {
        _labelLanguageFlow.value = languageCode
    }

    override fun getVoiceLanguage(): String = "en-US"

    override fun setVoiceLanguage(languageCode: String) {
        _voiceLanguageFlow.value = languageCode
    }
}