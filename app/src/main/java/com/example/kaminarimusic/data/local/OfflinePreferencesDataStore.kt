package com.example.kaminarimusic.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.offlineDataStore: DataStore<Preferences> by preferencesDataStore(name = "kaminari_offline_prefs")

data class OfflinePreferences(
    val isOfflineModeForced: Boolean = false,
    val isNavidromeConnected: Boolean = true,
    val autoCacheOnWifi: Boolean = true,
    val cacheSizeLimitMb: Int = 1024,
    val lastSyncTimestamp: Long = 0L
)

class OfflinePreferencesManager(private val context: Context) {
    companion object {
        val KEY_OFFLINE_MODE = booleanPreferencesKey("is_offline_mode_forced")
        val KEY_NAVIDROME_CONNECTED = booleanPreferencesKey("is_navidrome_connected")
        val KEY_AUTO_CACHE_WIFI = booleanPreferencesKey("auto_cache_on_wifi")
        val KEY_CACHE_LIMIT_MB = intPreferencesKey("cache_size_limit_mb")
        val KEY_LAST_SYNC_TS = longPreferencesKey("last_cache_sync_timestamp")
    }

    val offlinePreferencesFlow: Flow<OfflinePreferences> = context.offlineDataStore.data.map { prefs ->
        OfflinePreferences(
            isOfflineModeForced = prefs[KEY_OFFLINE_MODE] ?: false,
            isNavidromeConnected = prefs[KEY_NAVIDROME_CONNECTED] ?: true,
            autoCacheOnWifi = prefs[KEY_AUTO_CACHE_WIFI] ?: true,
            cacheSizeLimitMb = prefs[KEY_CACHE_LIMIT_MB] ?: 1024,
            lastSyncTimestamp = prefs[KEY_LAST_SYNC_TS] ?: 0L
        )
    }

    suspend fun setOfflineMode(forced: Boolean) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_OFFLINE_MODE] = forced
        }
    }

    suspend fun setNavidromeConnected(connected: Boolean) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_NAVIDROME_CONNECTED] = connected
        }
    }

    suspend fun setAutoCache(enabled: Boolean) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_AUTO_CACHE_WIFI] = enabled
        }
    }

    suspend fun updateSyncTimestamp(timestamp: Long = System.currentTimeMillis()) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC_TS] = timestamp
        }
    }
}
