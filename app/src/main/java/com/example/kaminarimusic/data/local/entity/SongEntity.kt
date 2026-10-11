package com.example.kaminarimusic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val serverId: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String? = null,
    val genre: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val duration: Long = 0L, // in seconds or ms
    val bitDepth: Int? = null,
    val sampleRate: Int? = null,
    val codec: String? = null,
    val bitrate: Int? = null,
    val coverArtUrl: String? = null,
    val localCoverArtPath: String? = null, // Path to local cached album art on disk
    val cachedArtworkPath: String? = null, // Alias for localCoverArtPath
    val localFilePath: String? = null, // Path to local cached audio file
    val isDownloaded: Boolean = false, // Ready for offline playback
    val isFavorite: Boolean = false,
    val rating: Int = 0,
    val lyrics: String? = null,
    val playCount: Int = 0,
    val syncState: String = "SYNCED" // SYNCED, PENDING, UPLOADING, FAILED
)
