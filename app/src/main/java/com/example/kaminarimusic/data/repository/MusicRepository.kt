package com.example.kaminarimusic.data.repository

import com.example.kaminarimusic.data.local.dao.MusicDao
import com.example.kaminarimusic.data.local.entity.AlbumEntity
import com.example.kaminarimusic.data.local.entity.ArtistEntity
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.data.local.entity.SyncOperationEntity
import com.example.kaminarimusic.data.network.MediaGatewayApi
import com.example.kaminarimusic.data.network.MetadataUpdateRequest
import com.example.kaminarimusic.data.network.NavidromeApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MusicRepository(
    private val musicDao: MusicDao,
    private val navidromeApi: NavidromeApi? = null,
    private val mediaGatewayApi: MediaGatewayApi? = null
) {
    val allSongs: Flow<List<SongEntity>> = musicDao.getAllSongs()
    val allAlbums: Flow<List<AlbumEntity>> = musicDao.getAllAlbums()
    val allArtists: Flow<List<ArtistEntity>> = musicDao.getAllArtists()
    val favoriteSongs: Flow<List<SongEntity>> = musicDao.getFavoriteSongs()
    val downloadedSongs: Flow<List<SongEntity>> = musicDao.getDownloadedSongs()
    val pendingOperations: Flow<List<SyncOperationEntity>> = musicDao.getPendingSyncOperations()

    suspend fun insertSong(song: SongEntity) = musicDao.insertSong(song)
    suspend fun updateSong(song: SongEntity) = musicDao.updateSong(song)
    suspend fun deleteSong(id: Long) {
        val song = musicDao.getSongById(id)
        if (song != null) {
            musicDao.deleteSong(id)
            if (mediaGatewayApi != null && song.serverId.isNotEmpty()) {
                try {
                    mediaGatewayApi.deleteMedia(song.serverId)
                } catch (e: Exception) {
                    musicDao.insertSyncOperation(
                        SyncOperationEntity(
                            entityId = song.id.toString(),
                            serverId = song.serverId,
                            type = "DELETE",
                            status = "PENDING",
                            errorMessage = e.message
                        )
                    )
                }
            }
        }
    }

    suspend fun toggleFavorite(songId: Long) {
        val song = musicDao.getSongById(songId)
        if (song != null) {
            val updated = song.copy(isFavorite = !song.isFavorite)
            musicDao.updateSong(updated)
        }
    }

    suspend fun updateMetadata(songId: Long, title: String, artist: String, album: String, genre: String?, year: Int?, lyrics: String?) {
        val song = musicDao.getSongById(songId) ?: return
        val updated = song.copy(title = title, artist = artist, album = album, genre = genre, year = year, lyrics = lyrics, syncState = "PENDING")
        musicDao.updateSong(updated)

        if (mediaGatewayApi != null && song.serverId.isNotEmpty()) {
            try {
                val resp = mediaGatewayApi.updateMetadata(
                    song.serverId,
                    MetadataUpdateRequest(title, artist, album, genre, year, null, lyrics)
                )
                if (resp.isSuccessful) {
                    musicDao.updateSong(updated.copy(syncState = "SYNCED"))
                } else {
                    queueSyncOp(song.id.toString(), song.serverId, "UPDATE_METADATA")
                }
            } catch (e: Exception) {
                queueSyncOp(song.id.toString(), song.serverId, "UPDATE_METADATA")
            }
        } else {
            queueSyncOp(song.id.toString(), song.serverId, "UPDATE_METADATA")
        }
    }

    private suspend fun queueSyncOp(entityId: String, serverId: String?, type: String) {
        musicDao.insertSyncOperation(
            SyncOperationEntity(
                entityId = entityId,
                serverId = serverId,
                type = type,
                status = "PENDING"
            )
        )
    }

    suspend fun refreshFromServer(user: String, token: String, salt: String) {
        if (navidromeApi == null) return
        try {
            val resp = navidromeApi.getAlbums(user = user, token = token, salt = salt)
            val albums = resp.subsonicResponse?.albumList2?.album ?: emptyList()
            val albumEntities = albums.map {
                AlbumEntity(
                    serverId = it.id,
                    title = it.title,
                    artist = it.artist,
                    year = it.year,
                    songCount = it.songCount ?: 0
                )
            }
            musicDao.insertAlbums(albumEntities)
        } catch (e: Exception) {
            // Handle network failure gracefully
        }
    }
}
