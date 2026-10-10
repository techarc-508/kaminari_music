package com.example.kaminarimusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.theme.*

@Composable
fun MiniPlayer(
    currentSong: SongEntity?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating glass surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.5f))
                .clip(RoundedCornerShape(20.dp))
                .background(KaminariDarkSurface.copy(alpha = 0.95f))
                .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(20.dp))
                .clickable { onClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Artwork
                KaminariArtwork(
                    title = currentSong.title,
                    artist = currentSong.artist,
                    modifier = Modifier.size(48.dp),
                    cornerRadius = 12.dp,
                    iconSize = 22.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Text Meta
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentSong.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = KaminariPrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentSong.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = KaminariSecondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${currentSong.codec ?: "FLAC"}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = KaminariHiResGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mini Circular Play Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(KaminariWhiteControl)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = KaminariDarkBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
