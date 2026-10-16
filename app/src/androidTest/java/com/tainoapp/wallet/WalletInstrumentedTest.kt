package com.tainoapp.wallet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WalletInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchesAndNavigatesToStatistics() {
        composeRule.onNodeWithText("Total Balance").assertIsDisplayed()

        composeRule.onNodeWithText("Statistics").performClick()
        composeRule.onNodeWithText("Statistics").assertIsSelected()
    }
}
