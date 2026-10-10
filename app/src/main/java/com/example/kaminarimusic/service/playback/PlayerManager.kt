package com.example.kaminarimusic.service.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.kaminarimusic.data.local.entity.SongEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerManager(context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .build()

    val exoPlayer: ExoPlayer = try {
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build().apply {
                skipSilenceEnabled = true
            }
    } catch (e: Exception) {
        ExoPlayer.Builder(context).build()
    }

    private val _currentSong = MutableStateFlow<SongEntity?>(null)
    val currentSong: StateFlow<SongEntity?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _queue = MutableStateFlow<List<SongEntity>>(emptyList())
    val queue: StateFlow<List<SongEntity>> = _queue.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var sleepTimerJob: Job? = null
    private var progressJob: Job? = null
    private val _sleepTimerMinutesRemaining = MutableStateFlow<Int?>(null)
    val sleepTimerMinutesRemaining: StateFlow<Int?> = _sleepTimerMinutesRemaining.asStateFlow()

    private val _gaplessPlaybackEnabled = MutableStateFlow(true)
    val gaplessPlaybackEnabled: StateFlow<Boolean> = _gaplessPlaybackEnabled.asStateFlow()

    private val _audioNormalizationEnabled = MutableStateFlow(false)
    val audioNormalizationEnabled: StateFlow<Boolean> = _audioNormalizationEnabled.asStateFlow()

    private val _crossfadeDurationMs = MutableStateFlow(0L)
    val crossfadeDurationMs: StateFlow<Long> = _crossfadeDurationMs.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    progressJob?.cancel()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    nextSong()
                }
            }
        })
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                try {
                    _currentPositionMs.value = exoPlayer.currentPosition
                } catch (_: Exception) {}
                delay(200L)
            }
        }
    }

    fun setGaplessPlayback(enabled: Boolean) {
        _gaplessPlaybackEnabled.value = enabled
        try {
            exoPlayer.skipSilenceEnabled = enabled
        } catch (_: Exception) {}
    }

    fun setCrossfadeDuration(durationMs: Long) {
        _crossfadeDurationMs.value = durationMs
    }

    fun setAudioNormalization(enabled: Boolean) {
        _audioNormalizationEnabled.value = enabled
        try {
            exoPlayer.setAudioAttributes(audioAttributes, enabled)
        } catch (_: Exception) {}
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _sleepTimerMinutesRemaining.value = null
            return
        }
        _sleepTimerMinutesRemaining.value = minutes
        sleepTimerJob = CoroutineScope(Dispatchers.Main).launch {
            for (m in minutes downTo 1) {
                _sleepTimerMinutesRemaining.value = m
                delay(60_000L)
            }
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                _isPlaying.value = false
            }
            _sleepTimerMinutesRemaining.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerMinutesRemaining.value = null
    }

    fun playSong(song: SongEntity, queueList: List<SongEntity> = listOf(song)) {
        _currentSong.value = song
        _queue.value = queueList
        
        val url = song.localFilePath ?: song.coverArtUrl ?: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        try {
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .build()
                )
                .build()

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _isPlaying.value = true
            startProgressTracker()
        } catch (e: Exception) {
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        try {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                _isPlaying.value = false
            } else {
                exoPlayer.play()
                _isPlaying.value = true
            }
        } catch (_: Exception) {}
    }

    fun nextSong() {
        val current = _currentSong.value ?: return
        val list = _queue.value
        val index = list.indexOfFirst { it.id == current.id }
        if (index != -1 && index + 1 < list.size) {
            playSong(list[index + 1], list)
        } else if (list.isNotEmpty()) {
            playSong(list.first(), list)
        }
    }

    fun previousSong() {
        val current = _currentSong.value ?: return
        val list = _queue.value
        val index = list.indexOfFirst { it.id == current.id }
        if (index > 0) {
            playSong(list[index - 1], list)
        } else if (list.isNotEmpty()) {
            playSong(list.last(), list)
        }
    }

    fun setVolume(vol: Float) {
        _volume.value = vol
        try {
            exoPlayer.volume = vol
        } catch (_: Exception) {}
    }

    fun seekTo(positionMs: Long) {
        try {
            exoPlayer.seekTo(positionMs)
            _currentPositionMs.value = positionMs
        } catch (_: Exception) {}
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerJob?.cancel()
        try {
            exoPlayer.release()
        } catch (_: Exception) {}
    }
}
