package com.tainoapp.wallet.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.data.BudgetCycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

data class AppLockSettings(
    val enabled: Boolean,
    val lastBackgroundTime: Long?
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val TOTAL_BALANCE = floatPreferencesKey("total_balance")
        val BUDGET_CYCLE = stringPreferencesKey("budget_cycle")
        val BUDGET_HISTORY_JSON = stringPreferencesKey("budget_history_json")
        val FUNDS_ADDED_HISTORY_JSON = stringPreferencesKey("funds_added_history_json") // New key
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LAST_BACKGROUND_TIME = longPreferencesKey("app_last_background_time")
        val ANALYTICS_CONSENT = booleanPreferencesKey("analytics_consent")
        const val TAG = "UserPrefsRepository"
    }

    val analyticsConsent: Flow<Boolean?> = dataStore.data
        .map { it[ANALYTICS_CONSENT] }

    val balance: Flow<Float> = dataStore.data
        .catch { if (it is IOException) { Log.e(TAG, "Error reading balance.", it); emit(emptyPreferences()) } else { throw it } }
        .map { it[TOTAL_BALANCE] ?: 0f }

    val budgetCycle: Flow<BudgetCycle> = dataStore.data
        .catch { if (it is IOException) { Log.e(TAG, "Error reading budget cycle.", it); emit(emptyPreferences()) } else { throw it } }
        .map { BudgetCycle.valueOf(it[BUDGET_CYCLE] ?: BudgetCycle.WEEKLY.name) }

    val appLockSettings: Flow<AppLockSettings> = dataStore.data
        .catch { if (it is IOException) { Log.e(TAG, "Error reading app lock settings.", it); emit(emptyPreferences()) } else { throw it } }
        .map { preferences ->
            AppLockSettings(
                enabled = preferences[APP_LOCK_ENABLED] ?: false,
                lastBackgroundTime = preferences[APP_LAST_BACKGROUND_TIME]
            )
        }

    val budgetHistory: Flow<Map<String, List<BudgetCategory>>> = dataStore.data
        .catch { if (it is IOException) { Log.e(TAG, "Error reading history.", it); emit(emptyPreferences()) } else { throw it } }
        .map { preferences ->
            val jsonString = preferences[BUDGET_HISTORY_JSON]
            if (jsonString.isNullOrEmpty()) {
                emptyMap()
            } else {
                Json.decodeFromString<Map<String, List<BudgetCategory>>>(jsonString)
            }
        }

    // New flow for funds added history
    val fundsAddedHistory: Flow<Map<String, Float>> = dataStore.data
        .catch { if (it is IOException) { Log.e(TAG, "Error reading funds added history.", it); emit(emptyPreferences()) } else { throw it } }
        .map { preferences ->
            val jsonString = preferences[FUNDS_ADDED_HISTORY_JSON]
            if (jsonString.isNullOrEmpty()) {
                emptyMap()
            } else {
                Json.decodeFromString<Map<String, Float>>(jsonString)
            }
        }

    suspend fun updateBalance(newBalance: Float) {
        dataStore.edit { it[TOTAL_BALANCE] = newBalance }
    }

    suspend fun updateBudgetCycle(budgetCycle: BudgetCycle) {
        dataStore.edit { it[BUDGET_CYCLE] = budgetCycle.name }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        dataStore.edit {
            it[APP_LOCK_ENABLED] = enabled
            it.remove(APP_LAST_BACKGROUND_TIME)
        }
    }

    suspend fun setAnalyticsConsent(enabled: Boolean) {
        dataStore.edit { it[ANALYTICS_CONSENT] = enabled }
    }

    suspend fun recordAppBackgrounded(timestamp: Long) {
        dataStore.edit {
            if (it[APP_LOCK_ENABLED] == true) {
                it[APP_LAST_BACKGROUND_TIME] = timestamp
            }
        }
    }

    suspend fun clearAppBackgroundTime() {
        dataStore.edit { it.remove(APP_LAST_BACKGROUND_TIME) }
    }

    suspend fun saveBudgetHistory(history: Map<String, List<BudgetCategory>>) {
        val jsonString = Json.encodeToString(history)
        dataStore.edit { it[BUDGET_HISTORY_JSON] = jsonString }
    }

    // New function to save the funds added history
    suspend fun saveFundsAddedHistory(history: Map<String, Float>) {
        val jsonString = Json.encodeToString(history)
        dataStore.edit { it[FUNDS_ADDED_HISTORY_JSON] = jsonString }
    }
}
