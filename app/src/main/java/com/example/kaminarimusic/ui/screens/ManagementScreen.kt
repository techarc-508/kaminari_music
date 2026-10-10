package com.example.kaminarimusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.kaminarimusic.ui.components.KaminariQualityBadge
import com.example.kaminarimusic.ui.theme.*

@Composable
fun ManagementScreen(
    song: SongEntity,
    onSave: (title: String, artist: String, album: String, genre: String?, year: Int?, lyrics: String?) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.artist) }
    var album by remember { mutableStateOf(song.album) }
    var genre by remember { mutableStateOf(song.genre ?: "") }
    var yearStr by remember { mutableStateOf(song.year?.toString() ?: "2026") }
    var lyrics by remember { mutableStateOf(song.lyrics ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KaminariPrimaryText)
            }

            Text(
                text = "Edit Track Metadata",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )

            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Artwork & Codec Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(KaminariDarkSurface)
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KaminariArtwork(
                    title = title,
                    artist = artist,
                    modifier = Modifier.size(72.dp),
                    cornerRadius = 16.dp,
                    iconSize = 32.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    KaminariQualityBadge(
                        codec = song.codec ?: "FLAC",
                        bitDepth = song.bitDepth ?: 24,
                        sampleRate = song.sampleRate ?: 96000
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bitrate: ${song.bitrate ?: 4608} kbps",
                        style = MaterialTheme.typography.labelSmall,
                        color = KaminariSecondaryText
                    )
                    Text(
                        text = "Duration: ${song.duration / 60}m ${song.duration % 60}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = KaminariSecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Track Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KaminariPrimary,
                unfocusedBorderColor = Color(0x22FFFFFF),
                focusedTextColor = KaminariPrimaryText,
                unfocusedTextColor = KaminariPrimaryText
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = artist,
            onValueChange = { artist = it },
            label = { Text("Artist") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KaminariPrimary,
                unfocusedBorderColor = Color(0x22FFFFFF),
                focusedTextColor = KaminariPrimaryText,
                unfocusedTextColor = KaminariPrimaryText
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = album,
            onValueChange = { album = it },
            label = { Text("Album") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KaminariPrimary,
                unfocusedBorderColor = Color(0x22FFFFFF),
                focusedTextColor = KaminariPrimaryText,
                unfocusedTextColor = KaminariPrimaryText
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KaminariPrimary,
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = KaminariPrimaryText,
                    unfocusedTextColor = KaminariPrimaryText
                )
            )

            OutlinedTextField(
                value = yearStr,
                onValueChange = { yearStr = it },
                label = { Text("Year") },
                modifier = Modifier.weight(0.6f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KaminariPrimary,
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = KaminariPrimaryText,
                    unfocusedTextColor = KaminariPrimaryText
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lyrics,
            onValueChange = { lyrics = it },
            label = { Text("Synced LRC or Plain Lyrics") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KaminariPrimary,
                unfocusedBorderColor = Color(0x22FFFFFF),
                focusedTextColor = KaminariPrimaryText,
                unfocusedTextColor = KaminariPrimaryText
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cancel", color = KaminariSecondaryText)
            }
            Button(
                onClick = {
                    onSave(title, artist, album, genre.ifBlank { null }, yearStr.toIntOrNull(), lyrics.ifBlank { null })
                    onBack()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KaminariPrimary)
            ) {
                Text("Save Metadata", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
