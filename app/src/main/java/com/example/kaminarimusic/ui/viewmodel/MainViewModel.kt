package com.example.kaminarimusic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaminarimusic.data.local.KaminariDatabase
import com.example.kaminarimusic.data.local.entity.AlbumEntity
import com.example.kaminarimusic.data.local.entity.ArtistEntity
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.data.repository.MusicRepository
import com.example.kaminarimusic.service.playback.PlayerManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = KaminariDatabase.getDatabase(application)
    private val repository = MusicRepository(database.musicDao())

    val playerManager = PlayerManager(application)

    val allSongs: StateFlow<List<SongEntity>> = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlbums: StateFlow<List<AlbumEntity>> = repository.allAlbums
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allArtists: StateFlow<List<ArtistEntity>> = repository.allArtists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<SongEntity>> = repository.favoriteSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedSongs: StateFlow<List<SongEntity>> = repository.downloadedSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingOperations = repository.pendingOperations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val existing = repository.allSongs.firstOrNull()
            if (existing.isNullOrEmpty()) {
                val sampleSongs = listOf(
                    SongEntity(
                        serverId = "s1",
                        title = "Uprising (Hi-Res Master)",
                        artist = "Muse",
                        album = "The Resistance",
                        albumArtist = "Muse",
                        genre = "Rock / Progressive",
                        year = 2009,
                        trackNumber = 1,
                        duration = 303L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:15.00] Paranoid is in bloom\n[00:20.00] The PR transmissions will resume\n[00:25.00] They'll try to push drugs that keep us all dumbed down\n[00:32.00] And hope that we will never see the truth around\n[00:40.00] They will not force us\n[00:44.00] They will stop degrading us\n[00:48.00] They will not control us\n[00:52.00] We will be victorious"
                    ),
                    SongEntity(
                        serverId = "s2",
                        title = "Resistance",
                        artist = "Muse",
                        album = "The Resistance",
                        albumArtist = "Muse",
                        genre = "Rock / Progressive",
                        year = 2009,
                        trackNumber = 2,
                        duration = 346L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:12.00] Is our secret safe tonight?\n[00:18.00] And are we out of sight?\n[00:24.00] Or will our world come tumbling down?\n[00:30.00] Love is our resistance"
                    ),
                    SongEntity(
                        serverId = "s3",
                        title = "Giorgio by Moroder (Studio Master)",
                        artist = "Daft Punk",
                        album = "Random Access Memories",
                        albumArtist = "Daft Punk",
                        genre = "Electro / Disco",
                        year = 2013,
                        trackNumber = 3,
                        duration = 544L,
                        bitDepth = 24,
                        sampleRate = 192000,
                        codec = "FLAC",
                        bitrate = 9216,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:10.00] When I was fifteen, sixteen, when I really started to play guitar\n[00:25.00] I wanted to make an album with the sound of the fifties\n[00:40.00] The sound of the sixties, of the seventies\n[00:55.00] And then have a sound of the future"
                    ),
                    SongEntity(
                        serverId = "s4",
                        title = "Get Lucky (Hi-Fi 24/96)",
                        artist = "Daft Punk ft. Pharrell Williams",
                        album = "Random Access Memories",
                        albumArtist = "Daft Punk",
                        genre = "Electro / Funk",
                        year = 2013,
                        trackNumber = 8,
                        duration = 369L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:20.00] Like the legend of the phoenix\n[00:25.00] All ends with beginnings\n[00:30.00] What keeps the planet spinning\n[00:35.00] The force from the beginning\n[00:40.00] We've come too far to give up who we are\n[00:46.00] So let's raise the bar and our cups to the stars"
                    ),
                    SongEntity(
                        serverId = "s5",
                        title = "Everything In Its Right Place",
                        artist = "Radiohead",
                        album = "Kid A",
                        albumArtist = "Radiohead",
                        genre = "Electronic / Alternative",
                        year = 2000,
                        trackNumber = 1,
                        duration = 251L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = false,
                        lyrics = "[00:10.00] Everything\n[00:15.00] Everything in its right place\n[00:25.00] Yesterday I woke up sucking a lemon\n[00:35.00] Everything in its right place"
                    ),
                    SongEntity(
                        serverId = "s6",
                        title = "Blinding Lights (Acoustic Hi-Res)",
                        artist = "The Weeknd",
                        album = "After Hours",
                        albumArtist = "The Weeknd",
                        genre = "Pop / Synthwave",
                        year = 2020,
                        trackNumber = 9,
                        duration = 200L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:14.00] I've been on my own for long enough\n[00:18.00] Maybe you can show me how to love, maybe\n[00:24.00] I'm going through withdrawals\n[00:28.00] You don't even have to do too much\n[00:34.00] You can turn me on with just a touch, baby\n[00:40.00] I look around and Sin City's cold and empty"
                    ),
                    SongEntity(
                        serverId = "s7",
                        title = "Time (Orchestral Master)",
                        artist = "Hans Zimmer",
                        album = "Inception (Original Score)",
                        albumArtist = "Hans Zimmer",
                        genre = "Classical / Cinematic",
                        year = 2010,
                        trackNumber = 12,
                        duration = 275L,
                        bitDepth = 24,
                        sampleRate = 192000,
                        codec = "FLAC",
                        bitrate = 9216,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = null
                    ),
                    SongEntity(
                        serverId = "s8",
                        title = "Numb (20th Anniversary Edition)",
                        artist = "Linkin Park",
                        album = "Meteora",
                        albumArtist = "Linkin Park",
                        genre = "Rock / Alternative",
                        year = 2003,
                        trackNumber = 13,
                        duration = 187L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:10.00] I'm tired of being what you want me to be\n[00:14.00] Feeling so faithless, lost under the surface\n[00:19.00] Don't know what you're expecting of me\n[00:23.00] Put under the pressure of walking in your shoes\n[00:28.00] I've become so numb, I can't feel you there\n[00:34.00] Become so tired, so much more aware"
                    ),
                    SongEntity(
                        serverId = "s9",
                        title = "Kaminari Prelude (Pure Ambient)",
                        artist = "Aetherium Hi-Fi",
                        album = "Storm Sessions 2026",
                        albumArtist = "Aetherium",
                        genre = "Ambient / Spatial",
                        year = 2026,
                        trackNumber = 1,
                        duration = 342L,
                        bitDepth = 24,
                        sampleRate = 96000,
                        codec = "FLAC",
                        bitrate = 4608,
                        isFavorite = true,
                        isDownloaded = true,
                        lyrics = "[00:12.00] Lightning echoes across the sonic spectrum\n[00:35.00] Pure spatial audio depth in every frequency"
                    ),
                    SongEntity(
                        serverId = "s10",
                        title = "Midnight Storm (DSD Studio Master)",
                        artist = "Aetherium Hi-Fi",
                        album = "Storm Sessions 2026",
                        albumArtist = "Aetherium",
                        genre = "Ambient / Electronic",
                        year = 2026,
                        trackNumber = 2,
                        duration = 280L,
                        bitDepth = 24,
                        sampleRate = 192000,
                        codec = "FLAC",
                        bitrate = 9216,
                        isFavorite = false,
                        isDownloaded = true,
                        lyrics = null
                    )
                )
                sampleSongs.forEach { repository.insertSong(it) }

                val sampleAlbums = listOf(
                    AlbumEntity(
                        serverId = "a1",
                        title = "The Resistance",
                        artist = "Muse",
                        year = 2009,
                        genre = "Progressive Rock",
                        songCount = 11,
                        codec = "FLAC",
                        bitDepth = 24,
                        sampleRate = 96000,
                        isFavorite = true,
                        isDownloaded = true
                    ),
                    AlbumEntity(
                        serverId = "a2",
                        title = "Random Access Memories",
                        artist = "Daft Punk",
                        year = 2013,
                        genre = "Electro / Funk",
                        songCount = 13,
                        codec = "FLAC",
                        bitDepth = 24,
                        sampleRate = 192000,
                        isFavorite = true,
                        isDownloaded = true
                    ),
                    AlbumEntity(
                        serverId = "a3",
                        title = "Kid A",
                        artist = "Radiohead",
                        year = 2000,
                        genre = "Alternative",
                        songCount = 10,
                        codec = "FLAC",
                        bitDepth = 24,
                        sampleRate = 96000,
                        isFavorite = true,
                        isDownloaded = false
                    ),
                    AlbumEntity(
                        serverId = "a4",
                        title = "After Hours",
                        artist = "The Weeknd",
                        year = 2020,
                        genre = "Synthwave / Pop",
                        songCount = 14,
                        codec = "FLAC",
                        bitDepth = 24,
                        sampleRate = 96000,
                        isFavorite = true,
                        isDownloaded = true
                    ),
                    AlbumEntity(
                        serverId = "a5",
                        title = "Inception Score",
                        artist = "Hans Zimmer",
                        year = 2010,
                        genre = "Classical / Score",
                        songCount = 12,
                        codec = "FLAC",
                        bitDepth = 24,
                        sampleRate = 192000,
                        isFavorite = true,
                        isDownloaded = true
                    ),
                    AlbumEntity(
                        serverId = "a6",
                        title = "Storm Sessions 2026",
                        artist = "Aetherium Hi-Fi",
                        year = 2026,
                        genre = "Ambient / Audiophile",
                        songCount = 8,
                        codec = "FLAC",
                        bitDepth = 24,
                        sampleRate = 96000,
                        isFavorite = true,
                        isDownloaded = true
                    )
                )
                sampleAlbums.forEach { database.musicDao().insertAlbum(it) }
            }
        }
    }

    fun playSong(song: SongEntity) {
        playerManager.playSong(song, allSongs.value)
    }

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(songId)
            val current = playerManager.currentSong.value
            if (current != null && current.id == songId) {
                val updated = current.copy(isFavorite = !current.isFavorite)
                database.musicDao().updateSong(updated)
                playerManager.playSong(updated, playerManager.queue.value)
            }
        }
    }

    fun updateMetadata(songId: Long, title: String, artist: String, album: String, genre: String?, year: Int?, lyrics: String?) {
        viewModelScope.launch {
            repository.updateMetadata(songId, title, artist, album, genre, year, lyrics)
        }
    }

    fun scanLocalMusic() {
        viewModelScope.launch {
            val localSongs = com.example.kaminarimusic.util.MusicScanner.scanDeviceForMusic(getApplication())
            localSongs.forEach { repository.insertSong(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
