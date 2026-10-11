package com.example.kaminarimusic.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
    onScanMusic: () -> Unit,
    isOfflineModeForced: Boolean = false,
    isNavidromeConnected: Boolean = true,
    cachedCount: Int = 0,
    cacheSizeMb: Float = 0f,
    onToggleOfflineMode: (Boolean) -> Unit = {},
    onToggleNavidromeConnected: (Boolean) -> Unit = {},
    onCacheAllOffline: () -> Unit = {},
    onClearOfflineCache: () -> Unit = {}
) {
    var navidromeUrl by remember { mutableStateOf("https://music.kaminari-hi-fi.internal") }
    var username by remember { mutableStateOf("audiophile_master") }
    var showCacheSuccessToast by remember { mutableStateOf(false) }
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

        // Offline Caching & Navidrome Resilience (Room & DataStore)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offline Playback & Storage",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = KaminariPrimaryText
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isNavidromeConnected) Color(0x2210B981) else Color(0x22F59E0B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isNavidromeConnected) Color(0x5510B981) else Color(0x55F59E0B)
                    )
                ) {
                    Text(
                        text = if (isNavidromeConnected) "ROOM + NAVIDROME" else "OFFLINE CACHE ONLY",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = if (isNavidromeConnected) Color(0xFF34D399) else KaminariHiResGold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(KaminariDarkSurface)
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Force Offline Mode Switch (DataStore)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Offline Playback Mode",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = KaminariPrimaryText
                            )
                            Text(
                                "Bypass server and play metadata/art from Room",
                                style = MaterialTheme.typography.labelSmall,
                                color = KaminariSecondaryText
                            )
                        }
                        Switch(
                            checked = isOfflineModeForced,
                            onCheckedChange = onToggleOfflineMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = KaminariPrimary
                            )
                        )
                    }

                    // Connection Simulation Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Navidrome Online State",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = KaminariPrimaryText
                            )
                            Text(
                                if (isNavidromeConnected) "Connection active" else "Connection lost - using offline cache",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isNavidromeConnected) Color(0xFF34D399) else KaminariHiResGold
                            )
                        }
                        Switch(
                            checked = isNavidromeConnected,
                            onCheckedChange = onToggleNavidromeConnected,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981),
                                uncheckedThumbColor = Color(0xFFF59E0B),
                                uncheckedTrackColor = Color(0x33F59E0B)
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0x14FFFFFF))

                    // Cache Stats Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Cached Metadata & Artwork",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = KaminariPrimaryText
                            )
                            Text(
                                text = "$cachedCount songs stored • ${String.format("%.1f", cacheSizeMb)} MB disk cache",
                                style = MaterialTheme.typography.labelSmall,
                                color = KaminariHiResGold
                            )
                        }
                    }

                    // Cache All Action Button
                    Button(
                        onClick = {
                            onCacheAllOffline()
                            showCacheSuccessToast = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = KaminariPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cache All Library Metadata & Art", fontWeight = FontWeight.Bold)
                    }

                    AnimatedVisibility(visible = showCacheSuccessToast) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x2210B981),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Metadata and album art successfully cached for offline use!", color = Color(0xFF34D399), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    // Clear Cache Action Button
                    OutlinedButton(
                        onClick = onClearOfflineCache,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KaminariSecondaryText)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Offline Cached Files")
                    }
                }
            }
        }

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
                        Text("Audio Equalizer (Jazz, Rock, Classical)", fontWeight = FontWeight.Bold)
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
