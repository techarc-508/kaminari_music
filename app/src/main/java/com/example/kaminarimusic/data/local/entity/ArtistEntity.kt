package com.example.kaminarimusic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val serverId: String,
    val name: String,
    val imageUrl: String? = null,
    val bio: String? = null,
    val albumCount: Int = 0,
    val isFavorite: Boolean = false
)
