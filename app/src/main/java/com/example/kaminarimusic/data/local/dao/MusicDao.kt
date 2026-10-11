package com.example.kaminarimusic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kaminarimusic.data.local.entity.AlbumEntity
import com.example.kaminarimusic.data.local.entity.ArtistEntity
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.data.local.entity.SyncOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    // Songs
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isFavorite = 1")
    fun getFavoriteSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isDownloaded = 1")
    fun getDownloadedSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isDownloaded = 1 OR localCoverArtPath IS NOT NULL")
    fun getOfflineCachedSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isDownloaded = 1 OR localCoverArtPath IS NOT NULL")
    fun getOfflineSongs(): Flow<List<SongEntity>>

    @Query("SELECT COUNT(*) FROM songs WHERE isDownloaded = 1 OR localCoverArtPath IS NOT NULL")
    fun getOfflineCachedCount(): Flow<Int>

    @Query("SELECT * FROM songs WHERE album = :albumName")
    fun getSongsForAlbum(albumName: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE artist = :artistName")
    fun getSongsForArtist(artistName: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE serverId = :serverId LIMIT 1")
    suspend fun getSongByServerId(serverId: String): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity): Long

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("UPDATE songs SET localCoverArtPath = :path WHERE id = :songId")
    suspend fun updateSongArtworkPath(songId: Long, path: String)

    @Query("UPDATE songs SET localFilePath = :filePath, isDownloaded = :isDownloaded WHERE id = :songId")
    suspend fun updateSongDownloadState(songId: Long, filePath: String?, isDownloaded: Boolean)

    @Query("UPDATE songs SET isDownloaded = :isDownloaded, localFilePath = :localPath, localCoverArtPath = :artPath WHERE id = :songId")
    suspend fun updateOfflineStatus(songId: Long, isDownloaded: Boolean, localPath: String?, artPath: String?)

    @Query("UPDATE albums SET localCoverArtPath = :artPath, isDownloaded = 1 WHERE id = :albumId")
    suspend fun updateAlbumOfflineStatus(albumId: Long, artPath: String?)

    @Query("UPDATE songs SET isDownloaded = 0, localCoverArtPath = NULL")
    suspend fun clearOfflineCachedSongs()

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Long)

    @Query("DELETE FROM songs WHERE serverId = :serverId")
    suspend fun deleteSongByServerId(serverId: String)

    // Albums
    @Query("SELECT * FROM albums")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE isFavorite = 1")
    fun getFavoriteAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE isDownloaded = 1")
    fun getDownloadedAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): AlbumEntity?

    @Query("SELECT * FROM albums WHERE serverId = :serverId LIMIT 1")
    suspend fun getAlbumByServerId(serverId: String): AlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity): Long

    @Update
    suspend fun updateAlbum(album: AlbumEntity)

    @Query("UPDATE albums SET localCoverArtPath = :path WHERE id = :albumId")
    suspend fun updateAlbumArtworkPath(albumId: Long, path: String)

    @Query("DELETE FROM albums WHERE id = :id")
    suspend fun deleteAlbum(id: Long)

    // Artists
    @Query("SELECT * FROM artists")
    fun getAllArtists(): Flow<List<ArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity): Long

    // Sync Operations
    @Query("SELECT * FROM sync_operations WHERE status != 'SYNCED'")
    fun getPendingSyncOperations(): Flow<List<SyncOperationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncOperation(operation: SyncOperationEntity): Long

    @Update
    suspend fun updateSyncOperation(operation: SyncOperationEntity)

    @Query("DELETE FROM sync_operations WHERE operationId = :id")
    suspend fun deleteSyncOperation(id: Long)
}
