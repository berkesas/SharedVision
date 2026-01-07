package com.scinforma.sharedvision.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scinforma.sharedvision.data.IUserPreferences
import com.scinforma.sharedvision.services.TTSManager
import com.scinforma.sharedvision.utils.Logger
import com.scinforma.sharedvision.generated.resources.*
import org.jetbrains.compose.resources.stringResource

private const val TAG = "RecognizedTextScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizedTextScreen(
    navController: NavController,
    userPreferences: IUserPreferences
) {
    // Get recognized text from navigation
    val recognizedText = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("recognized_text") ?: ""

    // Collect TTS settings
    val ttsEnabled by userPreferences.ttsEnabledFlow.collectAsState(initial = true)

    var isSpeaking by remember { mutableStateOf(false) }

    // Monitor speaking state
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(100)
            isSpeaking = TTSManager.isSpeaking()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.recognize)) },
                navigationIcon = {
                    IconButton(onClick = {
                        TTSManager.stop()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable text content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (recognizedText.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.no_text_recognized),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Text(
                            text = recognizedText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }

            // Read button section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Character count
                    Text(
                        text = "Characters: ${recognizedText.length}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Read/Stop button
                    Button(
                        onClick = {
                            if (isSpeaking) {
                                Logger.d(TAG, "Stopping TTS")
                                TTSManager.stop()
                            } else {
                                if (ttsEnabled && recognizedText.isNotEmpty()) {
                                    Logger.d(TAG, "Starting TTS for ${recognizedText.length} characters")
                                    TTSManager.speak(recognizedText)
                                }
                            }
                        },
                        enabled = recognizedText.isNotEmpty() && ttsEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = if (isSpeaking) stringResource(Res.string.stop_reading) else stringResource(Res.string.read_text)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSpeaking) stringResource(Res.string.stop_reading) else stringResource(Res.string.read_text),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (!ttsEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.tts_off),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Cleanup when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            TTSManager.stop()
        }
    }
}