package com.example.kaminarimusic.util

import android.content.Context
import android.provider.MediaStore
import com.example.kaminarimusic.data.local.entity.SongEntity
import java.io.File

object MusicScanner {
    fun scanDeviceForMusic(context: Context): List<SongEntity> {
        val songs = mutableListOf<SongEntity>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.MIME_TYPE
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataCol)
                val ext = File(path).extension.lowercase()
                
                // Only include common formats + hi-res candidates
                if (ext in listOf("mp3", "flac", "wav", "dsd", "dff", "dsf")) {
                    songs.add(
                        SongEntity(
                            serverId = "local_${path.hashCode()}",
                            title = cursor.getString(titleCol) ?: "Unknown",
                            artist = cursor.getString(artistCol) ?: "Unknown",
                            album = cursor.getString(albumCol) ?: "Unknown",
                            localFilePath = path,
                            duration = cursor.getLong(durationCol) / 1000,
                            codec = ext.uppercase(),
                            isDownloaded = true
                        )
                    )
                }
            }
        }
        return songs
    }
}
