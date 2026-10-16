package com.tainoapp.wallet

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tainoapp.wallet.repository.UserPreferencesRepository

private const val USER_PREFERENCES_NAME = "user_preferences"

// Create a DataStore instance, tied to the Application context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * The Application class for the Wallet app.
 * It holds the single instance of our repository.
 */
class WalletApplication : Application() {
    // The repository is lazy-initialized, so it's only created when first needed
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
