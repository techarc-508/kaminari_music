package com.example.kaminarimusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.ui.components.KaminariCategoryChip
import com.example.kaminarimusic.ui.theme.*

@Composable
fun EqualizerScreen(
    onBack: () -> Unit
) {
    val bands = listOf("32Hz", "64Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz")
    val bandValues = remember { mutableStateMapOf<String, Float>().apply { bands.forEach { put(it, 0f) } } }
    var selectedPreset by remember { mutableStateOf("Flat") }
    val presets = listOf("Flat", "Bass Boost", "Vocal Focus", "Hi-Res Pure", "Rock", "Electronic", "Jazz")

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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "10-Band Equalizer",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = KaminariPrimaryText
                )
                Text(
                    text = "32-Bit Floating Point DSP",
                    style = MaterialTheme.typography.labelSmall,
                    color = KaminariHiResGold
                )
            }

            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preset Chips
        Text(
            text = "Acoustic Presets",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = KaminariPrimaryText
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                KaminariCategoryChip(
                    text = preset,
                    isSelected = selectedPreset == preset,
                    onClick = {
                        selectedPreset = preset
                        when (preset) {
                            "Flat" -> bands.forEach { bandValues[it] = 0f }
                            "Bass Boost" -> {
                                bandValues["32Hz"] = 7.5f
                                bandValues["64Hz"] = 6.0f
                                bandValues["125Hz"] = 3.5f
                                bands.filter { it !in listOf("32Hz", "64Hz", "125Hz") }.forEach { bandValues[it] = 0f }
                            }
                            "Vocal Focus" -> {
                                bandValues["1kHz"] = 4.5f
                                bandValues["2kHz"] = 5.5f
                                bandValues["4kHz"] = 3.0f
                                bands.filter { it !in listOf("1kHz", "2kHz", "4kHz") }.forEach { bandValues[it] = 0f }
                            }
                            "Hi-Res Pure" -> {
                                bandValues["32Hz"] = 1.5f
                                bandValues["8kHz"] = 3.0f
                                bandValues["16kHz"] = 4.5f
                            }
                            else -> bands.forEach { bandValues[it] = 2.0f }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sliders Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(KaminariDarkSurface)
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                bands.forEach { band ->
                    val value = bandValues[band] ?: 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = band,
                            modifier = Modifier.width(55.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = KaminariPrimaryText
                        )
                        Slider(
                            value = value,
                            onValueChange = {
                                bandValues[band] = it
                                selectedPreset = "Custom"
                            },
                            valueRange = -12f..12f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = KaminariPrimary,
                                activeTrackColor = KaminariPrimary,
                                inactiveTrackColor = Color(0x22FFFFFF)
                            )
                        )
                        Text(
                            text = "${if (value >= 0) "+" else ""}${String.format("%.1f", value)}dB",
                            modifier = Modifier.width(60.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = if (value != 0f) KaminariHiResGold else KaminariSecondaryText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
