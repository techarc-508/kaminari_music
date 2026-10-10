package com.example.kaminarimusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.components.KaminariArtwork
import com.example.kaminarimusic.ui.components.KaminariSongRow
import com.example.kaminarimusic.ui.theme.*

data class SmartPlaylist(
    val id: String,
    val title: String,
    val description: String,
    val filter: (SongEntity) -> Boolean
)

@Composable
fun SmartPlaylistsScreen(
    songs: List<SongEntity>,
    onSongClick: (SongEntity) -> Unit,
    onToggleFavorite: (Long) -> Unit = {}
) {
    val smartPlaylists = remember {
        listOf(
            SmartPlaylist(
                id = "hires",
                title = "Hi-Res Masterpieces",
                description = "24-bit / 96kHz+ pure studio masters",
                filter = { (it.bitDepth ?: 16) >= 24 || (it.sampleRate ?: 44100) >= 96000 }
            ),
            SmartPlaylist(
                id = "favorites",
                title = "Audiophile Favorites",
                description = "Your most played & loved tracks",
                filter = { it.isFavorite || it.playCount > 0 }
            ),
            SmartPlaylist(
                id = "2026",
                title = "New Releases (2026)",
                description = "Modern lossless electronic & synth releases",
                filter = { it.year == 2026 }
            ),
            SmartPlaylist(
                id = "ambient",
                title = "Spatial & Ambient Soundscapes",
                description = "Deep acoustics, soundstage & dynamic range",
                filter = { it.genre?.contains("Ambient", ignoreCase = true) == true || it.genre?.contains("Audiophile", ignoreCase = true) == true }
            )
        )
    }

    var selectedPlaylist by remember { mutableStateOf<SmartPlaylist?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        if (selectedPlaylist == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KaminariPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = KaminariPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Smart Mixes",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = KaminariPrimaryText
                    )
                    Text(
                        text = "Dynamic audiophile collections",
                        style = MaterialTheme.typography.bodySmall,
                        color = KaminariSecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(smartPlaylists) { playlist ->
                    val count = songs.count(playlist.filter)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(KaminariDarkSurface)
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                            .clickable { selectedPlaylist = playlist }
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playlist.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KaminariPrimaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = playlist.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KaminariSecondaryText
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = KaminariPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KaminariPrimary.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "$count tracks",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KaminariPrimary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            val filteredSongs = songs.filter(selectedPlaylist!!.filter)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { selectedPlaylist = null },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x14FFFFFF))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KaminariPrimaryText)
                }

                Text(
                    text = selectedPlaylist!!.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = KaminariPrimaryText
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredSongs) { song ->
                    KaminariSongRow(
                        song = song,
                        isPlaying = false,
                        onClick = { onSongClick(song) },
                        onFavoriteClick = { onToggleFavorite(song.id) },
                        onMoreClick = {}
                    )
                }
            }
        }
    }
}
