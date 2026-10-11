package com.example.kaminarimusic.data.local.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.offlineDataStore: DataStore<Preferences> by preferencesDataStore(name = "kaminari_offline_cache_prefs")

class OfflineCachePreferences(private val context: Context) {

    companion object {
        val KEY_OFFLINE_MODE_FORCED = booleanPreferencesKey("offline_mode_forced")
        val KEY_AUTO_CACHE_ARTWORK = booleanPreferencesKey("auto_cache_artwork")
        val KEY_SERVER_CONNECTION_STATE = stringPreferencesKey("server_connection_state") // ONLINE, OFFLINE_FALLBACK, CONNECTING
        val KEY_LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        val KEY_CACHED_TRACKS_COUNT = intPreferencesKey("cached_tracks_count")
    }

    val isOfflineModeForced: Flow<Boolean> = context.offlineDataStore.data.map { prefs ->
        prefs[KEY_OFFLINE_MODE_FORCED] ?: false
    }

    val autoCacheArtwork: Flow<Boolean> = context.offlineDataStore.data.map { prefs ->
        prefs[KEY_AUTO_CACHE_ARTWORK] ?: true
    }

    val serverConnectionState: Flow<String> = context.offlineDataStore.data.map { prefs ->
        prefs[KEY_SERVER_CONNECTION_STATE] ?: "ONLINE"
    }

    val lastSyncTimestamp: Flow<Long> = context.offlineDataStore.data.map { prefs ->
        prefs[KEY_LAST_SYNC_TIMESTAMP] ?: 0L
    }

    val cachedTracksCount: Flow<Int> = context.offlineDataStore.data.map { prefs ->
        prefs[KEY_CACHED_TRACKS_COUNT] ?: 0
    }

    suspend fun setOfflineModeForced(forced: Boolean) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_OFFLINE_MODE_FORCED] = forced
        }
    }

    suspend fun setAutoCacheArtwork(auto: Boolean) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_AUTO_CACHE_ARTWORK] = auto
        }
    }

    suspend fun setServerConnectionState(state: String) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_SERVER_CONNECTION_STATE] = state
        }
    }

    suspend fun recordSync(tracksCount: Int) {
        context.offlineDataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC_TIMESTAMP] = System.currentTimeMillis()
            prefs[KEY_CACHED_TRACKS_COUNT] = tracksCount
        }
    }

    suspend fun clearPreferences() {
        context.offlineDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
