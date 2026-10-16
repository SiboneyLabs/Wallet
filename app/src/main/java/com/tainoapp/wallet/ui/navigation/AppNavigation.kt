package com.tainoapp.wallet.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tainoapp.wallet.ui.screens.GoalsScreen
import com.tainoapp.wallet.ui.screens.HomeScreen
import com.tainoapp.wallet.ui.screens.SettingsScreen
import com.tainoapp.wallet.ui.screens.StatisticsScreen
import com.tainoapp.wallet.ui.screens.settings.AboutDialog

// Final, simplified, and robust navigation logic
private fun navigateToTopLevelDestination(navController: NavController, screen: Screen) {
    navController.navigate(screen.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = false // Set restoreState to false to force recreation
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Statistics, Screen.Goals)
    val showNavRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val appNavHost: @Composable (Modifier) -> Unit = {
        NavHost(navController, startDestination = Screen.Home.route, modifier = it) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Statistics.route) { StatisticsScreen() }
            composable(Screen.Goals.route) { GoalsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }

    if (showNavRail) {
        Row {
            NavigationRail {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationRailItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navigateToTopLevelDestination(navController, screen) }
                    )
                }
            }
            appNavHost(Modifier)
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Wallet") },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Budget Cycle") }, onClick = { navController.navigate(Screen.Settings.route); showMenu = false })
                            DropdownMenuItem(text = { Text("About") }, onClick = { showAboutDialog = true; showMenu = false })
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = { navigateToTopLevelDestination(navController, screen) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            appNavHost(Modifier.padding(innerPadding))
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}
