package com.example.kaminarimusic.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.components.*
import com.example.kaminarimusic.ui.theme.*

@Composable
fun NowPlayingScreen(
    currentSong: SongEntity?,
    isPlaying: Boolean,
    volume: Float,
    onPlayPauseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onBack: () -> Unit,
    onSeek: (Long) -> Unit = {},
    onOpenEqualizer: () -> Unit = {}
) {
    if (currentSong == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KaminariDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No track playing", color = KaminariPrimaryText)
        }
        return
    }

    val haptic = LocalHapticFeedback.current
    var dragAmountX by remember { mutableFloatStateOf(0f) }
    var dragAmountY by remember { mutableFloatStateOf(0f) }

    var progress by remember { mutableFloatStateOf(0.35f) }
    var showLyrics by remember { mutableStateOf(false) }
    var isShuffleOn by remember { mutableStateOf(false) }
    var isRepeatOn by remember { mutableStateOf(false) }

    val totalSeconds = if (currentSong.duration > 0) currentSong.duration else 240L
    val currentSeconds = (totalSeconds * progress).toLong()
    val currentMillis = currentSeconds * 1000L
    val remainingSeconds = (totalSeconds - currentSeconds).coerceAtLeast(0L)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragAmountX > 80f) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPreviousClick()
                        } else if (dragAmountX < -80f) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNextClick()
                        }
                        dragAmountX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragAmountX += dragAmount
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragAmountY > 100f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBack()
                        }
                        dragAmountY = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        dragAmountY += dragAmount
                    }
                )
            }
    ) {
        // Ambient background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KaminariPrimary.copy(alpha = 0.15f),
                            KaminariDarkBackground.copy(alpha = 0.85f),
                            KaminariDarkBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Drag Pill & Minimize
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x14FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = KaminariPrimaryText,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Drag indicator pill
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOpenEqualizer()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Equalizer",
                            tint = KaminariPrimaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showLyrics = !showLyrics
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (showLyrics) KaminariPrimary.copy(alpha = 0.3f) else Color(0x14FFFFFF))
                    ) {
                        Icon(
                            imageVector = if (showLyrics) Icons.Filled.Notes else Icons.Outlined.Notes,
                            contentDescription = "Lyrics",
                            tint = if (showLyrics) KaminariPrimary else KaminariPrimaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Content: Large Artwork or Synced Lyrics
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
                    .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = KaminariPrimary.copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(32.dp))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (showLyrics) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(KaminariDarkSurface.copy(alpha = 0.95f))
                            .padding(16.dp)
                    ) {
                        LyricsView(
                            lyricsText = currentSong.lyrics,
                            currentPositionMs = currentMillis
                        )
                    }
                } else {
                    KaminariArtwork(
                        title = currentSong.title,
                        artist = currentSong.artist,
                        localArtworkPath = currentSong.localCoverArtPath,
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 32.dp,
                        iconSize = 80.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Song Info, Format Badge & Favorite Heart
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KaminariQualityBadge(
                    codec = currentSong.codec ?: "FLAC",
                    bitDepth = currentSong.bitDepth ?: 24,
                    sampleRate = currentSong.sampleRate ?: 96000
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentSong.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = KaminariPrimaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currentSong.artist} • ${currentSong.album}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KaminariSecondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFavoriteClick()
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                    ) {
                        Icon(
                            imageVector = if (currentSong.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (currentSong.isFavorite) KaminariTertiary else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Waveform Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                KaminariProgressWaveform(
                    progress = progress,
                    onSeek = { newProgress ->
                        progress = newProgress
                        val seekMs = (totalSeconds * newProgress * 1000).toLong()
                        onSeek(seekMs)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 38.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Time labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${currentSeconds / 60}:${String.format("%02d", currentSeconds % 60)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = KaminariPrimaryText
                    )
                    Text(
                        text = "-${remainingSeconds / 60}:${String.format("%02d", remainingSeconds % 60)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = KaminariSecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playback Controls Row (Shuffle, Prev, Play/Pause White Button, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = {
                        isShuffleOn = !isShuffleOn
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleOn) KaminariPrimary else KaminariSecondaryText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPreviousClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = KaminariPrimaryText,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Main Circular White Play Button
                KaminariPlayButton(
                    isPlaying = isPlaying,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPlayPauseClick()
                    },
                    size = 72.dp,
                    iconSize = 36.dp
                )

                // Next
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNextClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = KaminariPrimaryText,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat
                IconButton(
                    onClick = {
                        isRepeatOn = !isRepeatOn
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (isRepeatOn) KaminariPrimary else KaminariSecondaryText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
