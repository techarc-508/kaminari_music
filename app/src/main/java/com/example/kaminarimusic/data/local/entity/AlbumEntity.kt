package com.example.kaminarimusic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val serverId: String,
    val title: String,
    val artist: String,
    val year: Int? = null,
    val genre: String? = null,
    val coverArtUrl: String? = null,
    val localCoverArtPath: String? = null, // Path to local cached album art on disk
    val cachedArtworkPath: String? = null, // Alias
    val songCount: Int = 0,
    val duration: Long = 0L,
    val codec: String? = null,
    val bitDepth: Int? = null,
    val sampleRate: Int? = null,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false
)
