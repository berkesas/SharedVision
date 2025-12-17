package com.scinforma.sharedvision.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.scinforma.sharedvision.data.ILanguagePreferences
import com.scinforma.sharedvision.ui.icons.getImageIcon
import com.scinforma.sharedvision.ui.icons.getHomeIcon
import com.scinforma.sharedvision.ui.icons.getPersonIcon
import com.scinforma.sharedvision.ui.icons.getModelIcon
import com.scinforma.sharedvision.ui.icons.getTextIcon
import com.scinforma.sharedvision.ui.icons.getSettingsIcon

import com.scinforma.sharedvision.generated.resources.Res
import com.scinforma.sharedvision.generated.resources.home
import com.scinforma.sharedvision.generated.resources.models
import com.scinforma.sharedvision.generated.resources.settings
import com.scinforma.sharedvision.generated.resources.camera
import com.scinforma.sharedvision.generated.resources.text
import com.scinforma.sharedvision.generated.resources.image
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomNavigationBar(navController: NavController,languagePreferences: ILanguagePreferences) {
    val items = listOf(
        NavigationItem("home", stringResource(Res.string.home), getHomeIcon()),
        NavigationItem("runner", stringResource(Res.string.image), getImageIcon()),  // NEW TAB
//        NavigationItem("models", stringResource(Res.string.models), getModelIcon()),
        NavigationItem("text_recognizer", stringResource(Res.string.text), getTextIcon()),
        NavigationItem("settings", stringResource(Res.string.settings), getSettingsIcon())
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    // Simplified navigation - just navigate to the route
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}