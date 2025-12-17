package com.scinforma.sharedvision

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.scinforma.sharedvision.data.DefaultLanguagePreferences
import com.scinforma.sharedvision.data.ILanguagePreferences
import com.scinforma.sharedvision.data.LanguagePreferences
import com.scinforma.sharedvision.localization.LocaleHelper
import com.scinforma.sharedvision.ml.ModelManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    private lateinit var languagePreferences: ILanguagePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        languagePreferences = LanguagePreferences(this)

        // Get current model from preferences
        val currentModel = languagePreferences.getCurrentModel()
        val labelLanguage = languagePreferences.getLabelLanguage()

        // Initialize ModelManager once when activity is created
        ModelManager.initialize(this,currentModel, labelLanguage)

        setContent {
                App(languagePreferences)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Only cleanup when activity is truly destroyed (not configuration change)
        if (isFinishing) {
            ModelManager.cleanup()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase ?: return super.attachBaseContext(newBase)
        val prefs = LanguagePreferences(context)
        val languageCode = prefs.getSelectedLanguage()
        super.attachBaseContext(LocaleHelper.setLocale(context, languageCode))
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(languagePreferences = DefaultLanguagePreferences())
}