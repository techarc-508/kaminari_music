package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.components.BottomNavBar
import com.example.kaminarimusic.ui.components.MiniPlayer
import com.example.kaminarimusic.ui.screens.*
import com.example.kaminarimusic.ui.theme.KaminariDarkBackground
import com.example.kaminarimusic.ui.theme.KaminariMusicTheme
import com.example.kaminarimusic.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaminariMusicTheme {
                var currentRoute by remember { mutableStateOf("home") }
                var selectedSongForManagement by remember { mutableStateOf<SongEntity?>(null) }
                var showEqualizer by remember { mutableStateOf(false) }
                var showNowPlayingSheet by remember { mutableStateOf(false) }

                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val scope = rememberCoroutineScope()

                val songs by viewModel.allSongs.collectAsState()
                val albums by viewModel.allAlbums.collectAsState()
                val currentSong by viewModel.playerManager.currentSong.collectAsState()
                val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
                val volume by viewModel.playerManager.volume.collectAsState()
                val gaplessEnabled by viewModel.playerManager.gaplessPlaybackEnabled.collectAsState()
                val normalizationEnabled by viewModel.playerManager.audioNormalizationEnabled.collectAsState()
                val crossfadeDuration by viewModel.playerManager.crossfadeDurationMs.collectAsState()
                val sleepTimerMinutes by viewModel.playerManager.sleepTimerMinutesRemaining.collectAsState()
                val offlinePrefs by viewModel.offlinePreferences.collectAsState()
                val offlineCount by viewModel.offlineCount.collectAsState()
                val cacheSizeMb by viewModel.cacheSizeMb.collectAsState()

                BackHandler(enabled = showNowPlayingSheet || showEqualizer || selectedSongForManagement != null || currentRoute != "home") {
                    when {
                        showNowPlayingSheet -> {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showNowPlayingSheet = false
                            }
                        }
                        showEqualizer -> showEqualizer = false
                        selectedSongForManagement != null -> selectedSongForManagement = null
                        currentRoute != "home" -> currentRoute = "home"
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = KaminariDarkBackground,
                    contentColor = Color.White,
                    bottomBar = {
                        if (selectedSongForManagement == null && !showEqualizer && !showNowPlayingSheet) {
                            Column {
                                AnimatedVisibility(
                                    visible = currentSong != null,
                                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                                ) {
                                    MiniPlayer(
                                        currentSong = currentSong,
                                        isPlaying = isPlaying,
                                        onPlayPauseClick = { viewModel.playerManager.togglePlayPause() },
                                        onClick = { showNowPlayingSheet = true }
                                    )
                                }
                                BottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { currentRoute = it }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when {
                            showEqualizer -> {
                                EqualizerScreen(
                                    equalizerManager = viewModel.playerManager.equalizerManager,
                                    onBack = { showEqualizer = false }
                                )
                            }
                            selectedSongForManagement != null -> {
                                ManagementScreen(
                                    song = selectedSongForManagement!!,
                                    onSave = { title, artist, album, genre, year, lyrics ->
                                        viewModel.updateMetadata(selectedSongForManagement!!.id, title, artist, album, genre, year, lyrics)
                                    },
                                    onBack = { selectedSongForManagement = null }
                                )
                            }
                            else -> {
                                when (currentRoute) {
                                    "home" -> HomeScreen(
                                        songs = songs,
                                        albums = albums,
                                        onSongClick = {
                                            viewModel.playSong(it)
                                            showNowPlayingSheet = true
                                        },
                                        onAlbumClick = { /* Album detail / filter */ },
                                        onManageSong = { selectedSongForManagement = it },
                                        onNavigateSearch = { currentRoute = "search" },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        isOfflineMode = offlinePrefs.isOfflineModeForced,
                                        isNavidromeConnected = offlinePrefs.isNavidromeConnected
                                    )
                                    "library" -> LibraryScreen(
                                        songs = songs,
                                        albums = albums,
                                        onSongClick = {
                                            viewModel.playSong(it)
                                            showNowPlayingSheet = true
                                        },
                                        onAlbumClick = { /* Album detail / filter */ },
                                        onManageSong = { selectedSongForManagement = it },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                                    )
                                    "smart" -> SmartPlaylistsScreen(
                                        songs = songs,
                                        onSongClick = {
                                            viewModel.playSong(it)
                                            showNowPlayingSheet = true
                                        },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                                    )
                                    "search" -> SearchScreen(
                                        songs = songs,
                                        onSongClick = {
                                            viewModel.playSong(it)
                                            showNowPlayingSheet = true
                                        },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                                    )
                                    "settings" -> SettingsScreen(
                                        gaplessEnabled = gaplessEnabled,
                                        normalizationEnabled = normalizationEnabled,
                                        crossfadeDurationMs = crossfadeDuration,
                                        sleepTimerMinutes = sleepTimerMinutes,
                                        onToggleGapless = { viewModel.playerManager.setGaplessPlayback(it) },
                                        onToggleNormalization = { viewModel.playerManager.setAudioNormalization(it) },
                                        onCrossfadeChange = { viewModel.playerManager.setCrossfadeDuration(it) },
                                        onStartSleepTimer = { viewModel.playerManager.startSleepTimer(it) },
                                        onCancelSleepTimer = { viewModel.playerManager.cancelSleepTimer() },
                                        onOpenEqualizer = { showEqualizer = true },
                                        onScanMusic = { viewModel.scanLocalMusic() },
                                        isOfflineModeForced = offlinePrefs.isOfflineModeForced,
                                        isNavidromeConnected = offlinePrefs.isNavidromeConnected,
                                        cachedCount = offlineCount,
                                        cacheSizeMb = cacheSizeMb,
                                        onToggleOfflineMode = { viewModel.toggleForceOfflineMode(it) },
                                        onToggleNavidromeConnected = { viewModel.setNavidromeConnectionStatus(it) },
                                        onCacheAllOffline = { viewModel.cacheAllLibraryOffline() },
                                        onClearOfflineCache = { viewModel.clearOfflineCache() }
                                    )
                                }
                            }
                        }
                    }
                }

                // Modal Bottom Sheet for full Now Playing view
                if (showNowPlayingSheet && currentSong != null) {
                    ModalBottomSheet(
                        onDismissRequest = { showNowPlayingSheet = false },
                        sheetState = sheetState,
                        containerColor = KaminariDarkBackground,
                        scrimColor = Color.Black.copy(alpha = 0.7f),
                        dragHandle = null,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ) {
                        NowPlayingScreen(
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            volume = volume,
                            onPlayPauseClick = { viewModel.playerManager.togglePlayPause() },
                            onFavoriteClick = {
                                currentSong?.let { viewModel.toggleFavorite(it.id) }
                            },
                            onNextClick = { viewModel.playerManager.nextSong() },
                            onPreviousClick = { viewModel.playerManager.previousSong() },
                            onVolumeChange = { viewModel.playerManager.setVolume(it) },
                            onBack = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showNowPlayingSheet = false
                                }
                            },
                            onSeek = { seekMs -> viewModel.playerManager.seekTo(seekMs) },
                            onOpenEqualizer = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showNowPlayingSheet = false
                                    showEqualizer = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
