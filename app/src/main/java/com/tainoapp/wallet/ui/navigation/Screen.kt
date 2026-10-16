package com.tainoapp.wallet.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A sealed class representing the screens in the app.
 * @param route The route used for navigation.
 * @param title The title of the screen.
 * @param icon The icon for the navigation bar.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.Info)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AppLock : Screen("app_lock", "App Lock", Icons.Default.Settings)
}
