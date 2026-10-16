package com.tainoapp.wallet.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Statistics, Screen.Goals, Screen.Settings)
    val showNavRail = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

    if (showNavRail) {
        // Tablet layout with Navigation Rail
        Row {
            NavigationRail {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationRailItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Statistics.route) { StatisticsScreen() }
                composable(Screen.Goals.route) { GoalsScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
            }
        }
    } else {
        // Phone layout with Bottom Navigation Bar
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Statistics.route) { StatisticsScreen() }
                composable(Screen.Goals.route) { GoalsScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
            }
        }
    }
}
