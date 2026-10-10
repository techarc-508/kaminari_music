package com.example.kaminarimusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.ui.theme.*

@Composable
fun SettingsScreen(
    gaplessEnabled: Boolean,
    normalizationEnabled: Boolean,
    crossfadeDurationMs: Long,
    sleepTimerMinutes: Int?,
    onToggleGapless: (Boolean) -> Unit,
    onToggleNormalization: (Boolean) -> Unit,
    onCrossfadeChange: (Long) -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onScanMusic: () -> Unit
) {
    var navidromeUrl by remember { mutableStateOf("https://music.kaminari-hi-fi.internal") }
    var username by remember { mutableStateOf("audiophile_master") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Title Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = KaminariPrimaryText
        )

        // Audio Engine & Hi-Fi Playback Card
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Audio Engine & Processing",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(KaminariDarkSurface)
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Equalizer button
                    Button(
                        onClick = onOpenEqualizer,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = KaminariPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("10-Band Hi-Fi Equalizer", fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = Color(0x14FFFFFF))

                    // Gapless Playback Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Gapless Playback",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = KaminariPrimaryText
                            )
                            Text(
                                "Bit-perfect seamless transitions",
                                style = MaterialTheme.typography.labelSmall,
                                color = KaminariSecondaryText
                            )
                        }
                        Switch(
                            checked = gaplessEnabled,
                            onCheckedChange = onToggleGapless,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = KaminariPrimary
                            )
                        )
                    }

                    // Audio Normalization (ReplayGain) Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Audio Normalization",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = KaminariPrimaryText
                            )
                            Text(
                                "ReplayGain loudness matching",
                                style = MaterialTheme.typography.labelSmall,
                                color = KaminariSecondaryText
                            )
                        }
                        Switch(
                            checked = normalizationEnabled,
                            onCheckedChange = onToggleNormalization,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = KaminariPrimary
                            )
                        )
                    }

                    // Crossfade Duration Slider
                    Column {
                        Text(
                            text = "Crossfade Duration: ${(crossfadeDurationMs / 1000)}s",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = KaminariPrimaryText
                        )
                        Slider(
                            value = crossfadeDurationMs.toFloat(),
                            onValueChange = { onCrossfadeChange(it.toLong()) },
                            valueRange = 0f..5000f,
                            steps = 4,
                            colors = SliderDefaults.colors(
                                thumbColor = KaminariPrimary,
                                activeTrackColor = KaminariPrimary,
                                inactiveTrackColor = Color(0x22FFFFFF)
                            )
                        )
                    }
                }
            }
        }

        // Sleep Timer Card
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(KaminariDarkSurface)
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                if (sleepTimerMinutes != null) {
                    Column {
                        Text(
                            text = "Timer active: $sleepTimerMinutes minutes remaining",
                            color = KaminariHiResGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onCancelSleepTimer,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel Sleep Timer", color = KaminariTertiary)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 45, 60).forEach { mins ->
                            OutlinedButton(
                                onClick = { onStartSleepTimer(mins) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("${mins}m", color = KaminariPrimaryText)
                            }
                        }
                    }
                }
            }
        }

        // Server & Sync Card
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Self-Hosted Backend",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(KaminariDarkSurface)
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = navidromeUrl,
                        onValueChange = { navidromeUrl = it },
                        label = { Text("Navidrome Server URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KaminariPrimary,
                            unfocusedBorderColor = Color(0x22FFFFFF),
                            focusedTextColor = KaminariPrimaryText,
                            unfocusedTextColor = KaminariPrimaryText
                        )
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Subsonic Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KaminariPrimary,
                            unfocusedBorderColor = Color(0x22FFFFFF),
                            focusedTextColor = KaminariPrimaryText,
                            unfocusedTextColor = KaminariPrimaryText
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onScanMusic,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x1AFFFFFF))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = KaminariPrimaryText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rescan Local Storage & Cache", color = KaminariPrimaryText)
                    }
                }
            }
        }

        // Extra generous bottom space to ensure clean scrolling above mini player
        Spacer(modifier = Modifier.height(64.dp))
    }
}
