package com.example.kaminarimusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.data.local.entity.AlbumEntity
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.components.*
import com.example.kaminarimusic.ui.theme.*

@Composable
fun LibraryScreen(
    songs: List<SongEntity>,
    albums: List<AlbumEntity>,
    onSongClick: (SongEntity) -> Unit,
    onAlbumClick: (AlbumEntity) -> Unit,
    onManageSong: (SongEntity) -> Unit,
    onToggleFavorite: (Long) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Songs") }
    val tabs = listOf("Songs", "Albums", "Offline Cache", "Favorites")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        // Title & Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )
            Text(
                text = "${songs.size} tracks",
                style = MaterialTheme.typography.bodyMedium,
                color = KaminariSecondaryText
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Glass Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tabs.forEach { tab ->
                KaminariCategoryChip(
                    text = tab,
                    isSelected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            "Songs" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(songs, key = { it.id }) { song ->
                        Box(modifier = Modifier.animateItem()) {
                            KaminariSongRow(
                                song = song,
                                isPlaying = false,
                                onClick = { onSongClick(song) },
                                onFavoriteClick = { onToggleFavorite(song.id) },
                                onMoreClick = { onManageSong(song) }
                            )
                        }
                    }
                }
            }
            "Albums" -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(albums, key = { it.id }) { album ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(KaminariDarkSurface)
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                                .clickable { onAlbumClick(album) }
                                .padding(12.dp)
                                .animateItem()
                        ) {
                            Column {
                                KaminariArtwork(
                                    title = album.title,
                                    artist = album.artist ?: "",
                                    localArtworkPath = album.cachedArtworkPath ?: album.coverArtUrl,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    cornerRadius = 14.dp,
                                    iconSize = 40.dp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = album.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KaminariPrimaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = album.artist ?: "Unknown Artist",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KaminariSecondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${album.codec ?: "FLAC"} • ${album.songCount} tracks",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = KaminariHiResGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            "Offline Cache" -> {
                val cachedSongs = songs.filter { it.isDownloaded || it.cachedArtworkPath != null || it.localFilePath != null }
                if (cachedSongs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = KaminariSecondaryText, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No offline cached tracks yet", color = KaminariSecondaryText)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x2210B981),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3310B981))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${cachedSongs.size} tracks cached in Room Database with local album artwork",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                        }

                        items(cachedSongs, key = { it.id }) { song ->
                            Box(modifier = Modifier.animateItem()) {
                                KaminariSongRow(
                                    song = song,
                                    isPlaying = false,
                                    onClick = { onSongClick(song) },
                                    onFavoriteClick = { onToggleFavorite(song.id) },
                                    onMoreClick = { onManageSong(song) }
                                )
                            }
                        }
                    }
                }
            }
            "Favorites" -> {
                val favorites = songs.filter { it.isFavorite }
                if (favorites.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = KaminariTertiary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No favorite songs added yet", color = KaminariSecondaryText)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(favorites, key = { it.id }) { song ->
                            Box(modifier = Modifier.animateItem()) {
                                KaminariSongRow(
                                    song = song,
                                    isPlaying = false,
                                    onClick = { onSongClick(song) },
                                    onFavoriteClick = { onToggleFavorite(song.id) },
                                    onMoreClick = { onManageSong(song) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
