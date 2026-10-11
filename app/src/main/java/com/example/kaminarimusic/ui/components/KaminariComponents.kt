package com.example.kaminarimusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.theme.*
import kotlin.random.Random

/**
 * Premium Glassmorphic Surface container.
 * Uses translucent layers and subtle borders without blurring foreground contents.
 */
@Composable
fun KaminariGlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    backgroundColor: Color = Color(0x14FFFFFF),
    borderColor: Color = Color(0x1EFFFFFF),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

/**
 * Vibrant Artistic Artwork representation with audio-driven ambient gradients.
 */
@Composable
fun KaminariArtwork(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    iconSize: Dp = 36.dp,
    localArtworkPath: String? = null
) {
    val localFile = remember(localArtworkPath) {
        if (!localArtworkPath.isNullOrEmpty()) {
            val f = File(localArtworkPath)
            if (f.exists() && f.isFile && f.length() > 0) f else null
        } else null
    }

    if (localFile != null) {
        AsyncImage(
            model = localFile,
            contentDescription = "$title - $artist",
            modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
            contentScale = ContentScale.Crop
        )
    } else {
        // Generate deterministic rich gradient colors based on title hash
        val hash = (title.hashCode() xor artist.hashCode()).let { if (it < 0) -it else it }
        val gradientList = remember(hash) {
            val colorPairs = listOf(
                listOf(Color(0xFF5C4BEA), Color(0xFF8582AE), Color(0xFF1E1E38)),
                listOf(Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFF17172F)),
                listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF24243D)),
                listOf(Color(0xFF8B5CF6), Color(0xFFF43F5E), Color(0xFF17172F)),
                listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF1E1E38)),
                listOf(Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFF24243D))
            )
            colorPairs[hash % colorPairs.size]
        }

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(Brush.linearGradient(gradientList)),
            contentAlignment = Alignment.Center
        ) {
            // Subtle ambient inner overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0x80000000)),
                            radius = 200f
                        )
                    )
            )

            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * Audiophile Quality Badge (e.g. FLAC 24-bit 96kHz, DSD128, MQA)
 */
@Composable
fun KaminariQualityBadge(
    codec: String = "FLAC",
    bitDepth: Int? = 24,
    sampleRate: Int? = 96000,
    modifier: Modifier = Modifier
) {
    val rateText = sampleRate?.let { "${it / 1000}kHz" } ?: "96kHz"
    val text = "$codec • ${bitDepth ?: 24}-bit / $rateText"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = KaminariPrimary.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(1.dp, KaminariPrimary.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = KaminariHiResGold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Signature Circular White Play/Pause Button
 */
@Composable
fun KaminariPlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    iconSize: Dp = 32.dp
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "playBtnScale")

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(12.dp, CircleShape, spotColor = KaminariPrimary.copy(alpha = 0.5f))
            .clip(CircleShape)
            .background(KaminariWhiteControl)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = KaminariDarkBackground,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Animated Interactive Waveform Progress Component
 */
@Composable
fun KaminariProgressWaveform(
    progress: Float, // 0.0 to 1.0
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    barCount: Int = 36,
    height: Dp = 48.dp
) {
    // Generate organic pseudo-waveform bar heights
    val barHeights = remember(barCount) {
        val rand = Random(42)
        List(barCount) { 0.2f + rand.nextFloat() * 0.8f }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(newProgress)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            barHeights.forEachIndexed { index, relativeHeight ->
                val barProgress = index.toFloat() / barCount.toFloat()
                val isPlayed = barProgress <= progress

                val barColor by animateColorAsState(
                    targetValue = if (isPlayed) KaminariPrimary else Color(0x33FFFFFF),
                    label = "waveformColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.5.dp)
                        .fillMaxHeight(relativeHeight)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }
        }
    }
}

/**
 * Category Chip Filter Pill
 */
@Composable
fun KaminariCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) KaminariPrimary else Color(0x14FFFFFF)
    val textColor = if (isSelected) Color.White else KaminariSecondaryText
    val borderColor = if (isSelected) KaminariPrimary else Color(0x1AFFFFFF)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}

/**
 * Server Status Pill (Subtle & Informative)
 */
@Composable
fun KaminariServerStatus(
    isConnected: Boolean = true,
    serverName: String = "Navidrome",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        color = Color(0x18FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) Color(0xFF10B981) else Color(0xFFF59E0B))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isConnected) "Connected" else "Offline Cache",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = KaminariPrimaryText
            )
        }
    }
}

/**
 * Premium Song Row Component with quick actions
 */
@Composable
fun KaminariSongRow(
    song: SongEntity,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isPlaying) KaminariDarkSurface.copy(alpha = 0.8f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork
            KaminariArtwork(
                title = song.title,
                artist = song.artist,
                localArtworkPath = song.cachedArtworkPath ?: song.coverArtUrl,
                modifier = Modifier.size(50.dp),
                cornerRadius = 10.dp,
                iconSize = 24.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isPlaying) KaminariPrimary else KaminariPrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = KaminariSecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${song.codec ?: "FLAC"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = KaminariHiResGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Favorite Button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) KaminariTertiary else KaminariSecondaryText.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Options / Menu
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Song options",
                    tint = KaminariSecondaryText.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
