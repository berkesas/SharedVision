package com.scinforma.sharedvision.data

import kotlinx.coroutines.flow.StateFlow

interface ILanguagePreferences {
    val selectedLanguageFlow: StateFlow<String?>
    val autoReadIntervalFlow: StateFlow<Long>
    val ttsEnabledFlow: StateFlow<Boolean>
    val currentModelFlow: StateFlow<String>
    val labelLanguageFlow: StateFlow<String>
    val voiceLanguageFlow: StateFlow<String>

    fun getSelectedLanguage(): String?
    fun setSelectedLanguage(languageCode: String?)

    fun getAutoReadInterval(): Long
    fun setAutoReadInterval(intervalMs: Long)

    fun getTtsEnabled(): Boolean
    fun setTtsEnabled(enabled: Boolean)

    fun getCurrentModel(): String
    fun setCurrentModel(modelPath: String)

    fun getLabelLanguage(): String
    fun setLabelLanguage(languageCode: String)

    fun getVoiceLanguage(): String
    fun setVoiceLanguage(languageCode: String)
}