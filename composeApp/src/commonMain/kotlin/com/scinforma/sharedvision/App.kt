package com.scinforma.sharedvision

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scinforma.sharedvision.ui.screens.HomeScreen
import com.scinforma.sharedvision.ui.screens.ProfileScreen
import com.scinforma.sharedvision.ui.navigation.BottomNavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.sharedvision.ui.AppTheme
import com.scinforma.sharedvision.data.ILanguagePreferences
import com.scinforma.sharedvision.ui.screens.ModelManagementScreen
import com.scinforma.sharedvision.ui.screens.ModelRunnerScreen
import com.scinforma.sharedvision.ui.screens.RecognizedTextScreen
import com.scinforma.sharedvision.ui.screens.SettingsScreen
import com.scinforma.sharedvision.ui.screens.TextRecognizerScreen

@Composable
@Preview
fun App(languagePreferences: ILanguagePreferences) {
    AppTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController, languagePreferences)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("runner/{model}") { backStackEntry ->
                    val modelPath = backStackEntry.arguments?.getString("model")
                    ModelRunnerScreen(languagePreferences,modelPath.toString())
                }
                composable("runner") {
                    ModelRunnerScreen(languagePreferences)
                }
                composable("models") {
                    ModelManagementScreen()
                }
                composable("profile") {
                    ProfileScreen()
                }
                composable("settings") {
                    val context = LocalContext.current
                    SettingsScreen(
                        languagePreferences = languagePreferences,
                        onLanguageChanged = { languageCode ->
                            languagePreferences.setSelectedLanguage(languageCode)
                            // Recreate activity to apply language change immediately
                            (context as? ComponentActivity)?.recreate()
                        }
                    )
                }
                // Text Recognition Routes
                composable("text_recognizer") {
                    TextRecognizerScreen(
                        navController = navController,
                        languagePreferences = languagePreferences
                    )
                }
                composable("recognized_text") {
                    RecognizedTextScreen(
                        navController = navController,
                        languagePreferences = languagePreferences
                    )
                }
            }
        }
    }
}