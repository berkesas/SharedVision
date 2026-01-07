package com.scinforma.sharedvision

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.scinforma.sharedvision.data.DefaultUserPreferences
import com.scinforma.sharedvision.data.IUserPreferences
import com.scinforma.sharedvision.data.UserPreferences
import com.scinforma.sharedvision.localization.LocaleHelper
import com.scinforma.sharedvision.ml.ModelManager

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: IUserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)

        // Get current model from preferences
        val currentModel = userPreferences.getCurrentModel()
        val labelLanguage = userPreferences.getLabelLanguage()

        // Initialize ModelManager once when activity is created
        ModelManager.initialize(this,currentModel, labelLanguage)

        setContent {
                App(userPreferences)
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
        val prefs = UserPreferences(context)
        val languageCode = prefs.getSelectedLanguage()
        super.attachBaseContext(LocaleHelper.setLocale(context, languageCode))
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(userPreferences = DefaultUserPreferences())
}