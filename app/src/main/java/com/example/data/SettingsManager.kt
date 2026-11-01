package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kiki_settings")

class SettingsManager(context: Context) {
    private val dataStore = context.applicationContext.dataStore

    object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val CACHE_ENABLED = booleanPreferencesKey("cache_enabled")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
    }

    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.DARK_MODE] ?: true }

    val cacheEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.CACHE_ENABLED] ?: true }

    val offlineModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.OFFLINE_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun setCacheEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CACHE_ENABLED] = enabled
        }
    }

    suspend fun setOfflineMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_MODE] = enabled
        }
    }
}
