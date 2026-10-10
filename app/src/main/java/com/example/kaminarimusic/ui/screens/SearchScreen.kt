package com.example.kaminarimusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.kaminarimusic.ui.components.KaminariCategoryChip
import com.example.kaminarimusic.ui.components.KaminariSongRow
import com.example.kaminarimusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    songs: List<SongEntity>,
    onSongClick: (SongEntity) -> Unit,
    onToggleFavorite: (Long) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val quickFilters = listOf("All", "Hi-Res FLAC", "24-bit / 96kHz", "Ambient", "Rock", "Electronic")
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredSongs = remember(query, selectedFilter, songs) {
        var list = if (query.isBlank()) songs else songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true) ||
                    it.genre?.contains(query, ignoreCase = true) == true
        }

        if (selectedFilter != "All") {
            list = when (selectedFilter) {
                "Hi-Res FLAC" -> list.filter { it.codec.equals("FLAC", ignoreCase = true) }
                "24-bit / 96kHz" -> list.filter { (it.bitDepth ?: 16) >= 24 && (it.sampleRate ?: 44100) >= 96000 }
                else -> list.filter { it.genre?.contains(selectedFilter, ignoreCase = true) == true }
            }
        }
        list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        // Title
        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = KaminariPrimaryText,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Large Rounded Glass Search Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(KaminariDarkSurface)
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search songs, artists, albums, FLAC...",
                        color = KaminariSecondaryText.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = KaminariPrimary
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = KaminariSecondaryText
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = KaminariPrimaryText,
                    unfocusedTextColor = KaminariPrimaryText,
                    cursorColor = KaminariPrimary
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Tag Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickFilters) { filter ->
                KaminariCategoryChip(
                    text = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Results Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (query.isBlank() && selectedFilter == "All") "Browse Library" else "Results",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )
            Text(
                text = "${filteredSongs.size} found",
                style = MaterialTheme.typography.bodySmall,
                color = KaminariSecondaryText
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results List
        if (filteredSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = KaminariSecondaryText,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No results found for \"$query\"",
                        color = KaminariSecondaryText,
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                items(filteredSongs, key = { it.id }) { song ->
                    Box(modifier = Modifier.animateItem()) {
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
}
