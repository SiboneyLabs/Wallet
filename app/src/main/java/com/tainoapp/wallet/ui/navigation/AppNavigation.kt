package com.tainoapp.wallet.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tainoapp.wallet.WalletApplication
import com.tainoapp.wallet.ui.screens.AppLockScreen
import com.tainoapp.wallet.ui.screens.AppLockSettingsScreen
import com.tainoapp.wallet.ui.screens.HomeScreen
import com.tainoapp.wallet.ui.screens.SettingsScreen
import com.tainoapp.wallet.ui.screens.StatisticsScreen
import com.tainoapp.wallet.ui.screens.settings.AnalyticsConsentDialog
import com.tainoapp.wallet.ui.screens.settings.AboutDialog
import com.tainoapp.wallet.util.AppLockAuthentication
import kotlinx.coroutines.launch

private fun navigateToTopLevelDestination(navController: NavController, screen: Screen) {
    navController.navigate(screen.route) {
        popUpTo(navController.graph.findStartDestination().id)
        launchSingleTop = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val application = context.applicationContext as WalletApplication
    val repository = application.userPreferencesRepository
    val coroutineScope = rememberCoroutineScope()
    val analyticsConsent by repository.analyticsConsent.collectAsState(initial = null)
    val appLockSettings by repository.appLockSettings.collectAsState(initial = null)
    val appLockEnabled = appLockSettings?.enabled ?: false
    val lastBackgroundAt = appLockSettings?.lastBackgroundTime
    val navController = rememberNavController()
    // Goals screen removed from the list
    val screens = listOf(Screen.Home, Screen.Statistics)
    val showNavRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var hasAuthenticatedThisSession by remember { mutableStateOf(false) }
    var lifecycleStarted by remember { mutableStateOf(false) }
    var lockError by remember { mutableStateOf<String?>(null) }
    var promptRequest by remember { mutableIntStateOf(0) }
    var foregroundCheck by remember { mutableIntStateOf(0) }
    val latestAppLockEnabled by rememberUpdatedState(appLockEnabled)

    LaunchedEffect(analyticsConsent) {
        analyticsConsent?.let(application::applyAnalyticsConsent)
    }

    DisposableEffect(activity.lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    val timestamp = System.currentTimeMillis()
                    if (latestAppLockEnabled) {
                        coroutineScope.launch {
                            repository.recordAppBackgrounded(timestamp)
                        }
                    }
                }
                Lifecycle.Event.ON_START -> {
                    lifecycleStarted = true
                    foregroundCheck++
                }
                else -> Unit
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(foregroundCheck, appLockEnabled, lastBackgroundAt) {
        val backgroundTime = lastBackgroundAt
        if (foregroundCheck > 0 && appLockEnabled && backgroundTime != null) {
            val elapsed = System.currentTimeMillis() - backgroundTime
            if (elapsed >= 60_000L || elapsed < 0L) {
                isLocked = true
                lockError = null
            } else if (elapsed >= 0L) {
                repository.clearAppBackgroundTime()
            }
        }
    }

    val elapsedSinceBackground = lastBackgroundAt?.let {
        System.currentTimeMillis() - it
    }
    val lockDue = lifecycleStarted && appLockSettings != null && appLockEnabled &&
        elapsedSinceBackground != null &&
        (elapsedSinceBackground >= 60_000L || elapsedSinceBackground < 0L)
    val showLock = isLocked ||
        (lifecycleStarted && appLockSettings != null && appLockEnabled &&
            !hasAuthenticatedThisSession) ||
        lockDue

    DisposableEffect(showLock) {
        application.interstitialAdManager.isSuppressed = showLock
        onDispose { application.interstitialAdManager.isSuppressed = false }
    }

    LaunchedEffect(showLock, promptRequest) {
        if (showLock) {
            val prompt = BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        isLocked = false
                        hasAuthenticatedThisSession = true
                        lockError = null
                        coroutineScope.launch {
                            repository.clearAppBackgroundTime()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        lockError = "Authentication failed. Try again."
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        lockError = errString.toString()
                    }
                }
            )
            try {
                prompt.authenticate(AppLockAuthentication.createPromptInfo())
            } catch (_: IllegalArgumentException) {
                lockError = "Biometric or device screen-lock authentication is unavailable."
            }
        }
    }

    BackHandler(enabled = showLock) {}

    val appNavHost: @Composable (Modifier) -> Unit = {
        NavHost(navController, startDestination = Screen.Home.route, modifier = it) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Statistics.route) { StatisticsScreen() }
            // Goals composable removed
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.AppLock.route) { AppLockSettingsScreen() }
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (!lifecycleStarted || appLockSettings == null) {
            AppLockScreen(
                error = null,
                onRetry = {},
                isLoading = true
            )
        } else if (showNavRail) {
            Row(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
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
                                DropdownMenuItem(text = { Text("App Lock") }, onClick = {
                                    navController.navigate(Screen.AppLock.route)
                                    showMenu = false
                                })
                                DropdownMenuItem(text = { Text("Rate the app") }, onClick = {
                                    showMenu = false
                                    val marketIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=${context.packageName}")
                                    )
                                    try {
                                        context.startActivity(marketIntent)
                                    } catch (_: ActivityNotFoundException) {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                            )
                                        )
                                    }
                                })
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

        if (showLock) {
            AppLockScreen(
                error = lockError,
                onRetry = {
                    lockError = null
                    promptRequest++
                }
            )
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
    if (analyticsConsent == null) {
        AnalyticsConsentDialog(
            onChoice = { enabled ->
                coroutineScope.launch {
                    application.updateAnalyticsConsent(enabled)
                }
            }
        )
    }
}
