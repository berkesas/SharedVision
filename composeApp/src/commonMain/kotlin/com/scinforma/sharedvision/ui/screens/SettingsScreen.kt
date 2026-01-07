package com.scinforma.sharedvision.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scinforma.sharedvision.data.IUserPreferences
import com.scinforma.sharedvision.data.Language
import com.scinforma.sharedvision.data.ReadInterval
import com.scinforma.sharedvision.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import java.util.Locale

object LanguageProvider {

    /**
     * Get all available label languages from system locales
     */
    fun getAvailableLabelLanguages(): List<Language> {
        return Locale.getAvailableLocales()
            .distinctBy { it.language }
            .sortedBy { it.displayLanguage }
            .map { locale ->
                Language(
                    code = locale.language,
                    displayName = locale.displayLanguage
                )
            }
    }

    /**
     * Get all available TTS voice languages
     * Note: This creates a temporary TTS instance, so use sparingly
     */
    fun getAvailableVoiceLanguages(context: Context): List<Language> {
        // Common TTS languages that are usually available
        return listOf(
            Language(code = "en-US", displayName = "English (United States)"),
            Language(code = "en-GB", displayName = "English (United Kingdom)"),
            Language(code = "es-ES", displayName = "Español (España)"),
            Language(code = "es-US", displayName = "Español (Estados Unidos)"),
            Language(code = "fr-FR", displayName = "Français (France)"),
            Language(code = "de-DE", displayName = "Deutsch (Deutschland)"),
            Language(code = "it-IT", displayName = "Italiano (Italia)"),
            Language(code = "pt-BR", displayName = "Português (Brasil)"),
            Language(code = "ru-RU", displayName = "Русский (Россия)"),
            Language(code = "ja-JP", displayName = "日本語 (日本)"),
            Language(code = "ko-KR", displayName = "한국어 (대한민국)"),
            Language(code = "zh-CN", displayName = "中文 (中国)"),
            Language(code = "zh-TW", displayName = "中文 (台灣)"),
            Language(code = "ar-SA", displayName = "العربية (السعودية)"),
            Language(code = "hi-IN", displayName = "हिन्दी (भारत)"),
            Language(code = "tr-TR", displayName = "Türkçe (Türkiye)"),
            Language(code = "tk-TM", displayName = "Türkmençe (Türkmenistan)")
        ).sortedBy { it.displayName }
    }

    /**
     * Get display name for a label language code
     */
    fun getLabelLanguageDisplayName(languageCode: String): String {
        return Locale.forLanguageTag(languageCode).displayLanguage
    }

    /**
     * Get display name for a voice language code
     */
    fun getVoiceLanguageDisplayName(languageCode: String): String {
        return Locale.forLanguageTag(languageCode).displayName
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userPreferences: IUserPreferences,
    onLanguageChanged: (String?) -> Unit
) {
    val context = LocalContext.current

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showLabelLanguageDialog by remember { mutableStateOf(false) }
    var showVoiceLanguageDialog by remember { mutableStateOf(false) }

    val currentLanguage by userPreferences.selectedLanguageFlow.collectAsState()
    val currentInterval by userPreferences.autoReadIntervalFlow.collectAsState()
    val ttsEnabled by userPreferences.ttsEnabledFlow.collectAsState()
    val currentLabelLanguage by userPreferences.labelLanguageFlow.collectAsState(initial = "en")
    val currentVoiceLanguage by userPreferences.voiceLanguageFlow.collectAsState(initial = "en-US")
    val currentPredictionThreshold by userPreferences.predictionThresholdFlow.collectAsState()
    val currentAccessCode by userPreferences.accessCodeFlow.collectAsState()
    val systemDefaultText = stringResource(Res.string.system_default)

    // Define available languages
    val availableLanguages = remember(systemDefaultText) {
        listOf(
            Language(code = "system", displayName = systemDefaultText, isSystemDefault = true),
            Language(code = "en-US", displayName = "English (US)"),
            Language(code = "ru-RU", displayName = "Русский (Россия)"),
            Language(code = "tk-TM", displayName = "Türkmençe (Türkmenistan)")
        )
    }

    val availableLabelLanguages = remember {
        LanguageProvider.getAvailableLabelLanguages()
    }

    val availableVoiceLanguages = remember {
        LanguageProvider.getAvailableVoiceLanguages(context)
    }

    // Define available intervals
    val availableIntervals = remember {
        listOf(
            ReadInterval(3000L, "3"),
            ReadInterval(5000L, "5"),
            ReadInterval(6000L, "6"),
            ReadInterval(7000L, "7"),
            ReadInterval(8000L, "8"),
            ReadInterval(9000L, "9"),
            ReadInterval(10000L, "10")
        )
    }

    val selectedLanguageDisplay = remember(currentLanguage, availableLanguages) {
        availableLanguages.find {
            if (currentLanguage == null) it.isSystemDefault
            else it.code == currentLanguage
        }?.displayName ?: systemDefaultText
    }

    val selectedIntervalDisplay = remember(currentInterval, availableIntervals) {
        availableIntervals.find { it.milliseconds == currentInterval }?.displayName
            ?: "${currentInterval / 1000.0} seconds"
    }

    val selectedLabelLanguageDisplay = remember(currentLabelLanguage, availableLabelLanguages) {
        availableLabelLanguages.find { it.code == currentLabelLanguage }?.displayName
            ?: Locale.Builder().setLanguage(currentLabelLanguage).build().displayLanguage
    }

    val selectedVoiceLanguageDisplay = remember(currentVoiceLanguage, availableVoiceLanguages) {
        availableVoiceLanguages.find { it.code == currentVoiceLanguage }?.displayName
            ?: Locale.forLanguageTag(currentVoiceLanguage).displayName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Language Selection Item
            SettingsItem(
                title = stringResource(Res.string.language),
                subtitle = selectedLanguageDisplay,
                onClick = { showLanguageDialog = true }
            )

            HorizontalDivider()

            // Label Language Selection Item
            SettingsItem(
                title = stringResource(Res.string.labels_language),
                subtitle = selectedLabelLanguageDisplay,
                onClick = { showLabelLanguageDialog = true }
            )

            HorizontalDivider()

            // Voice Language Selection Item
            SettingsItem(
                title = stringResource(Res.string.voice_language),
                subtitle = selectedVoiceLanguageDisplay,
                onClick = { showVoiceLanguageDialog = true }
            )

            HorizontalDivider()

            // Auto Read Interval Item
            SettingsItem(
                title = stringResource(Res.string.automatic_read_interval),
                subtitle = selectedIntervalDisplay,
                onClick = { showIntervalDialog = true }
            )

            HorizontalDivider()

            // TTS Toggle Item
            SettingsItemWithSwitch(
                title = stringResource(Res.string.text_to_speech),
                subtitle = if (ttsEnabled) stringResource(Res.string.enabled) else stringResource(Res.string.disabled),
                checked = ttsEnabled,
                onCheckedChange = { enabled ->
                    userPreferences.setTtsEnabled(enabled)
                }
            )

            HorizontalDivider()

            SettingsItemWithTextInput(
                title = stringResource(Res.string.prediction_threshold),
                subtitle = "",
                value = currentPredictionThreshold.toString(),
                onValueChange = { newValue ->
                    newValue.toLongOrNull()?.let { userPreferences.setPredictionThreshold(it) }
                },
                placeholder = "0-100",
                unit = "%",
                inputFieldWidth = 80.dp
            )

            HorizontalDivider()

            SettingsItemWithTextInput(
                title = stringResource(Res.string.access_code),
                subtitle = "",
                value = currentAccessCode,
                onValueChange = { newValue ->
                    userPreferences.setAccessCode(newValue)
                }
            )

        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            languages = availableLanguages,
            currentLanguage = currentLanguage,
            onLanguageSelected = { selectedCode ->
                val languageToSave = if (selectedCode == "system") null else selectedCode
                onLanguageChanged(languageToSave)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showLabelLanguageDialog) {
        LanguageSelectionDialog(
            title = stringResource(Res.string.labels_language),
            languages = availableLabelLanguages,
            currentLanguage = currentLabelLanguage,
            onLanguageSelected = { selectedCode ->
                userPreferences.setLabelLanguage(selectedCode)
                showLabelLanguageDialog = false
            },
            onDismiss = { showLabelLanguageDialog = false }
        )
    }

    if (showVoiceLanguageDialog) {
        LanguageSelectionDialog(
            title = stringResource(Res.string.voice_language),
            languages = availableVoiceLanguages,
            currentLanguage = currentVoiceLanguage,
            onLanguageSelected = { selectedCode ->
                userPreferences.setVoiceLanguage(selectedCode)
                showVoiceLanguageDialog = false
            },
            onDismiss = { showVoiceLanguageDialog = false }
        )
    }

    if (showIntervalDialog) {
        IntervalSelectionDialog(
            intervals = availableIntervals,
            currentInterval = currentInterval,
            onIntervalSelected = { selectedInterval ->
                userPreferences.setAutoReadInterval(selectedInterval)
                showIntervalDialog = false
            },
            onDismiss = { showIntervalDialog = false }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsItemWithTextInput(
    title: String,
    subtitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    unit: String = "",
    inputFieldWidth: Dp = 120.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            keyboardOptions = keyboardOptions,
            singleLine = true,
            modifier = Modifier.width(inputFieldWidth)
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsItemWithSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    title: String = stringResource(Res.string.select_language),
    languages: List<Language>,
    currentLanguage: String?,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(languages) { language ->
                    val isSelected = if (language.isSystemDefault) {
                        currentLanguage == null
                    } else {
                        currentLanguage == language.code
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language.code) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (isSelected) {
                            RadioButton(
                                selected = true,
                                onClick = { onLanguageSelected(language.code) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}

@Composable
fun IntervalSelectionDialog(
    intervals: List<ReadInterval>,
    currentInterval: Long,
    onIntervalSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.select_automatic_read_interval)) },
        text = {
            LazyColumn {
                items(intervals) { interval ->
                    val isSelected = currentInterval == interval.milliseconds

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIntervalSelected(interval.milliseconds) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = interval.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (isSelected) {
                            RadioButton(
                                selected = true,
                                onClick = { onIntervalSelected(interval.milliseconds) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}