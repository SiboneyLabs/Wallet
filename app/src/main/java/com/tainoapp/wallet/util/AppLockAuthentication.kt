package com.tainoapp.wallet.util

import android.app.KeyguardManager
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

object AppLockAuthentication {
    fun isAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        return keyguardManager.isDeviceSecure ||
            biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Wallet")
            .setSubtitle("Use biometrics or your device screen lock")
            .setDeviceCredentialAllowed(true)
            .build()
    }
}
