package com.tainoapp.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tainoapp.wallet.ui.theme.WalletTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WalletTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    Greeting("Android", windowSizeClass.widthSizeClass)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, widthSizeClass: WindowWidthSizeClass, modifier: Modifier = Modifier) {
    val text = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> "Hello $name! (Compact)"
        WindowWidthSizeClass.Medium -> "Hello $name! (7-inch tablet)"
        WindowWidthSizeClass.Expanded -> "Hello $name! (10-inch tablet)"
        else -> "Hello $name!"
    }
    Text(
        text = text,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        Greeting("Android", WindowWidthSizeClass.Compact)
    }
}
