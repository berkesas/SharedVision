package com.scinforma.sharedvision

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sharedvision.ui.AppTheme
import com.scinforma.sharedvision.data.IUserPreferences
import com.scinforma.sharedvision.ui.navigation.BottomNavigationBar
import com.scinforma.sharedvision.ui.screens.HomeScreen
import com.scinforma.sharedvision.ui.screens.ModelManagementScreen
import com.scinforma.sharedvision.ui.screens.ModelRunnerScreen
import com.scinforma.sharedvision.ui.screens.RecognizedTextScreen
import com.scinforma.sharedvision.ui.screens.SettingsScreen
import com.scinforma.sharedvision.ui.screens.TextRecognizerScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(userPreferences: IUserPreferences) {
    AppTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController, userPreferences)
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
                    ModelRunnerScreen(userPreferences,modelPath.toString())
                }
                composable("runner") {
                    ModelRunnerScreen(userPreferences)
                }
                composable("models") {
                    ModelManagementScreen()
                }
//                composable("profile") {
//                    ProfileScreen()
//                }
                composable("settings") {
                    val context = LocalContext.current
                    SettingsScreen(
                        userPreferences = userPreferences,
                        onLanguageChanged = { languageCode ->
                            userPreferences.setSelectedLanguage(languageCode)
                            // Recreate activity to apply language change immediately
                            (context as? ComponentActivity)?.recreate()
                        }
                    )
                }
                // Text Recognition Routes
                composable("text_recognizer") {
                    TextRecognizerScreen(
                        navController = navController,
                        userPreferences = userPreferences
                    )
                }
                composable("recognized_text") {
                    RecognizedTextScreen(
                        navController = navController,
                        userPreferences = userPreferences
                    )
                }
            }
        }
    }
}