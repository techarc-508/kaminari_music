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

    @Query("SELECT * FROM songs WHERE album = :albumName")
    fun getSongsForAlbum(albumName: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE artist = :artistName")
    fun getSongsForArtist(artistName: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity): Long

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Long)

    @Query("DELETE FROM songs WHERE serverId = :serverId")
    suspend fun deleteSongByServerId(serverId: String)

    // Albums
    @Query("SELECT * FROM albums")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE isFavorite = 1")
    fun getFavoriteAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): AlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity): Long

    @Update
    suspend fun updateAlbum(album: AlbumEntity)

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
