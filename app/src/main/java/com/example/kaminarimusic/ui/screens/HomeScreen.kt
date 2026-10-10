package com.example.kaminarimusic.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.data.local.entity.AlbumEntity
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.ui.components.*
import com.example.kaminarimusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    songs: List<SongEntity>,
    albums: List<AlbumEntity>,
    onSongClick: (SongEntity) -> Unit,
    onAlbumClick: (AlbumEntity) -> Unit,
    onManageSong: (SongEntity) -> Unit,
    onNavigateSearch: () -> Unit = {},
    onToggleFavorite: (Long) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Trending") }
    val categories = listOf("Trending", "Rock", "Hip Hop", "Electro", "Pop", "Jazz", "Classical", "Hi-Res Master")

    var selectedSongForMenu by remember { mutableStateOf<SongEntity?>(null) }
    var showActionSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Dynamic scroll offset calculation for parallax and alpha
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 40 }
    }

    val topBarAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 0.95f else 0.0f,
        label = "topBarAlpha"
    )

    val filteredSongs = remember(selectedCategory, songs) {
        when (selectedCategory) {
            "Trending" -> songs
            "Hi-Res Master" -> songs.filter { (it.bitDepth ?: 16) >= 24 || (it.sampleRate ?: 44100) >= 96000 }
            else -> {
                val match = songs.filter { it.genre?.contains(selectedCategory, ignoreCase = true) == true }
                if (match.isEmpty()) songs else match
            }
        }
    }

    Scaffold(
        containerColor = KaminariDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 80.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Trending Right Now Header with subtle scroll parallax
                item {
                    val headerAlpha = remember {
                        derivedStateOf {
                            val offset = listState.firstVisibleItemScrollOffset
                            val index = listState.firstVisibleItemIndex
                            if (index == 0) (1f - (offset / 400f)).coerceIn(0.2f, 1f) else 1f
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .graphicsLayer { alpha = headerAlpha.value }
                    ) {
                        Text(
                            text = "Trending right now",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = KaminariPrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lossless masters hand-picked for this week",
                            style = MaterialTheme.typography.bodySmall,
                            color = KaminariSecondaryText
                        )
                    }
                }

                // Trending Featured Carousel with parallax and scale effects
                item {
                    val featuredSongs = songs.take(5)
                    val carouselState = rememberLazyListState()

                    LazyRow(
                        state = carouselState,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(featuredSongs) { song ->
                            // Featured Card (Width 290dp for partial reveal)
                            Box(
                                modifier = Modifier
                                    .width(290.dp)
                                    .height(190.dp)
                                    .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = KaminariPrimary.copy(alpha = 0.4f))
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable { onSongClick(song) }
                            ) {
                                // Artistic Background gradient
                                KaminariArtwork(
                                    title = song.title,
                                    artist = song.artist,
                                    modifier = Modifier.fillMaxSize(),
                                    cornerRadius = 24.dp,
                                    iconSize = 48.dp
                                )

                                // Glass card overlay on bottom
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color(0xCC17172F), Color(0xF217172F))
                                            )
                                        )
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            KaminariQualityBadge(
                                                codec = song.codec ?: "FLAC",
                                                bitDepth = song.bitDepth ?: 24,
                                                sampleRate = song.sampleRate ?: 96000
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = song.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = KaminariPrimaryText,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = song.artist,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = KaminariSecondaryText,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        // Circular White Play Button
                                        KaminariPlayButton(
                                            isPlaying = false,
                                            onClick = { onSongClick(song) },
                                            size = 46.dp,
                                            iconSize = 22.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Category Filter Chips
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { category ->
                            KaminariCategoryChip(
                                text = category,
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }

                // Section Header: Top Songs
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular Tracks",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = KaminariPrimaryText
                        )
                        Text(
                            text = "${filteredSongs.size} tracks",
                            style = MaterialTheme.typography.bodySmall,
                            color = KaminariSecondaryText
                        )
                    }
                }

                // Songs List with item entrance animations
                items(filteredSongs, key = { it.id }) { song ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateItem()
                    ) {
                        KaminariSongRow(
                            song = song,
                            isPlaying = false,
                            onClick = { onSongClick(song) },
                            onFavoriteClick = { onToggleFavorite(song.id) },
                            onMoreClick = {
                                selectedSongForMenu = song
                                showActionSheet = true
                            }
                        )
                    }
                }

                // Album Spotlights
                if (albums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Featured Albums",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = KaminariPrimaryText,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(albums, key = { it.id }) { album ->
                                Box(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(KaminariDarkSurface)
                                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                                        .clickable { onAlbumClick(album) }
                                        .padding(12.dp)
                                        .animateItem()
                                ) {
                                    Column {
                                        KaminariArtwork(
                                            title = album.title,
                                            artist = album.artist ?: "",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(136.dp),
                                            cornerRadius = 12.dp,
                                            iconSize = 36.dp
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
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Glassmorphic Collapsible Top Bar with Dynamic Scroll Blurring & Translucency
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(KaminariDarkBackground.copy(alpha = topBarAlpha))
                    .border(
                        1.dp,
                        if (isScrolled) Color(0x1AFFFFFF) else Color.Transparent,
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KaminariPrimary.copy(alpha = 0.2f))
                                .border(1.dp, KaminariPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = KaminariPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kaminari",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = KaminariPrimaryText
                            )
                            Text(
                                text = "Hi-Res Music",
                                style = MaterialTheme.typography.labelSmall,
                                color = KaminariSecondaryText
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KaminariServerStatus(
                            isConnected = true,
                            serverName = "Navidrome"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onNavigateSearch,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x18FFFFFF))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = KaminariPrimaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Action Bottom Sheet for Song
    if (showActionSheet && selectedSongForMenu != null) {
        val song = selectedSongForMenu!!
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            containerColor = KaminariDarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KaminariArtwork(
                        title = song.title,
                        artist = song.artist,
                        modifier = Modifier.size(54.dp),
                        cornerRadius = 12.dp,
                        iconSize = 24.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KaminariPrimaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${song.artist} • ${song.album}",
                            style = MaterialTheme.typography.bodySmall,
                            color = KaminariSecondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0x1AFFFFFF))
                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                ListItem(
                    headlineContent = { Text("Play Now", color = KaminariPrimaryText) },
                    leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = KaminariPrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        onSongClick(song)
                        showActionSheet = false
                    }
                )
                ListItem(
                    headlineContent = { Text(if (song.isFavorite) "Remove from Favorites" else "Add to Favorites", color = KaminariPrimaryText) },
                    leadingContent = { Icon(if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = KaminariTertiary) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        onToggleFavorite(song.id)
                        showActionSheet = false
                    }
                )
                ListItem(
                    headlineContent = { Text("Edit Metadata", color = KaminariPrimaryText) },
                    leadingContent = { Icon(Icons.Default.Edit, contentDescription = null, tint = KaminariSecondaryText) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        onManageSong(song)
                    }
                )
                ListItem(
                    headlineContent = { Text("Download Offline", color = KaminariPrimaryText) },
                    leadingContent = { Icon(Icons.Default.Download, contentDescription = null, tint = KaminariSecondaryText) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        showActionSheet = false
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
