package com.example.kaminarimusic.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaminarimusic.service.playback.EqualizerBand
import com.example.kaminarimusic.service.playback.EqualizerManager
import com.example.kaminarimusic.ui.components.KaminariCategoryChip
import com.example.kaminarimusic.ui.theme.*

@Composable
fun EqualizerScreen(
    equalizerManager: EqualizerManager,
    onBack: () -> Unit
) {
    val isEnabled by equalizerManager.isEnabled.collectAsState()
    val currentPreset by equalizerManager.currentPreset.collectAsState()
    val bands by equalizerManager.bands.collectAsState()
    val audioSessionId by equalizerManager.audioSessionId.collectAsState()
    val isHardwareAttached by equalizerManager.isHardwareAttached.collectAsState()

    val presetDescriptions = mapOf(
        "Jazz" to "Warm resonant bass, smooth midrange & airy harmonic sparkle",
        "Rock" to "Dynamic punchy low-end, scooped mids & soaring crisp treble",
        "Classical" to "Natural acoustic timbre, wide dynamic range & open concert soundstage",
        "Flat" to "Bit-perfect uncolored studio reference frequency response",
        "Bass Boost" to "Deep low-frequency drive with extended sub-bass impact",
        "Vocal Focus" to "Forward midrange clarity for enhanced vocals & acoustic presence",
        "Custom" to "Custom multi-band frequency curves adjusted for your listening setup"
    )

    val bandRoles = listOf(
        "Sub / Bass",
        "Mid-Bass",
        "Midrange",
        "Presence",
        "Treble / Air"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KaminariDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0x18FFFFFF))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = KaminariPrimaryText
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Audio Equalizer",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = KaminariPrimaryText
                )
                Text(
                    text = "ExoPlayer DSP Engine",
                    style = MaterialTheme.typography.labelSmall,
                    color = KaminariHiResGold
                )
            }

            // Power Switch
            Switch(
                checked = isEnabled,
                onCheckedChange = { equalizerManager.setEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = KaminariPrimary,
                    uncheckedThumbColor = Color(0x88FFFFFF),
                    uncheckedTrackColor = Color(0x22FFFFFF)
                )
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ExoPlayer Audio Session Status Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(KaminariDarkSurface)
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KaminariPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = KaminariPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (audioSessionId > 0) "Session ID: #$audioSessionId" else "ExoPlayer Master Session",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = KaminariPrimaryText
                        )
                        Text(
                            text = if (isEnabled) "Real-time hardware equalization active" else "Equalizer is currently bypassed",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isEnabled) KaminariSecondaryText else KaminariSecondaryText.copy(alpha = 0.5f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isEnabled && isHardwareAttached) Color(0x2210B981) else Color(0x22F59E0B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isEnabled && isHardwareAttached) Color(0x5510B981) else Color(0x55F59E0B)
                    )
                ) {
                    Text(
                        text = if (isEnabled && isHardwareAttached) "ATTACHED" else if (isEnabled) "DSP READY" else "BYPASS",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = if (isEnabled && isHardwareAttached) Color(0xFF34D399) else KaminariHiResGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preset Profiles Section
        Text(
            text = "Preset Profiles",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = KaminariPrimaryText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = presetDescriptions[currentPreset] ?: "Select an acoustic profile",
            style = MaterialTheme.typography.bodySmall,
            color = KaminariSecondaryText,
            minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Preset Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            equalizerManager.availablePresets.forEach { preset ->
                val isSelected = currentPreset == preset
                KaminariCategoryChip(
                    text = preset,
                    isSelected = isSelected,
                    onClick = {
                        equalizerManager.applyPreset(preset)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Frequency Response Curve Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(KaminariDarkSurface)
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Frequency Response Curve",
                        style = MaterialTheme.typography.labelSmall,
                        color = KaminariSecondaryText
                    )
                    Text(
                        text = "+${equalizerManager.maxDb.toInt()}dB / ${equalizerManager.minDb.toInt()}dB",
                        style = MaterialTheme.typography.labelSmall,
                        color = KaminariSecondaryText.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                EqCurveView(
                    bands = bands,
                    minDb = equalizerManager.minDb,
                    maxDb = equalizerManager.maxDb,
                    isEnabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Frequency Band Sliders Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Manual Band Adjustment",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KaminariPrimaryText
            )

            TextButton(
                onClick = { equalizerManager.resetToFlat() }
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = KaminariPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Flat", color = KaminariPrimary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(KaminariDarkSurface)
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .alpha(if (isEnabled) 1f else 0.45f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                bands.forEachIndexed { idx, band ->
                    val roleLabel = bandRoles.getOrElse(idx) { "" }
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = band.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KaminariPrimaryText
                                )
                                if (roleLabel.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• $roleLabel",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KaminariSecondaryText
                                    )
                                }
                            }

                            Text(
                                text = "${if (band.levelDb > 0) "+" else ""}${String.format("%.1f", band.levelDb)} dB",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (band.levelDb != 0f) KaminariHiResGold else KaminariSecondaryText
                            )
                        }

                        Slider(
                            value = band.levelDb,
                            onValueChange = { newDb ->
                                if (isEnabled) {
                                    equalizerManager.setBandLevel(band.index, newDb)
                                }
                            },
                            valueRange = equalizerManager.minDb..equalizerManager.maxDb,
                            enabled = isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = if (band.levelDb != 0f) KaminariHiResGold else KaminariPrimary,
                                activeTrackColor = KaminariPrimary,
                                inactiveTrackColor = Color(0x22FFFFFF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun EqCurveView(
    bands: List<EqualizerBand>,
    minDb: Float,
    maxDb: Float,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (bands.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val midY = h / 2f
        val range = (maxDb - minDb).coerceAtLeast(1f)

        // Draw 0dB center guideline
        drawLine(
            color = Color(0x33FFFFFF),
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1.dp.toPx()
        )

        // Calculate points
        val stepX = w / (bands.size - 1).coerceAtLeast(1)
        val points = bands.mapIndexed { index, band ->
            val normalized = ((maxDb - band.levelDb) / range).coerceIn(0f, 1f)
            val y = normalized * h
            val x = index * stepX
            Offset(x, y)
        }

        // Draw filled gradient area under curve
        val fillPath = Path().apply {
            moveTo(0f, h)
            lineTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
            lineTo(w, h)
            close()
        }

        val curveColor = if (isEnabled) KaminariPrimary else Color(0x669E9EB0)
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    curveColor.copy(alpha = 0.35f),
                    curveColor.copy(alpha = 0.05f),
                    Color.Transparent
                )
            )
        )

        // Draw curve stroke
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = strokePath,
            color = curveColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw point dots
        points.forEach { pt ->
            drawCircle(
                color = if (isEnabled) KaminariHiResGold else Color(0xFF9E9EB0),
                radius = 4.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = KaminariDarkBackground,
                radius = 2.dp.toPx(),
                center = pt
            )
        }
    }
}
