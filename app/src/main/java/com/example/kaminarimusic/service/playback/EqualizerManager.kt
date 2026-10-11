package com.example.kaminarimusic.service.playback

import android.content.Context
import android.media.audiofx.Equalizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class EqualizerBand(
    val index: Int,
    val centerFreqHz: Int,
    val label: String,
    val levelDb: Float
)

class EqualizerManager(private val context: Context) {

    private val tag = "EqualizerManager"

    private var equalizer: Equalizer? = null
    private var currentSessionId: Int = 0

    private val _isEnabled = MutableStateFlow(true)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _currentPreset = MutableStateFlow("Jazz")
    val currentPreset: StateFlow<String> = _currentPreset.asStateFlow()

    private val _audioSessionId = MutableStateFlow(0)
    val audioSessionId: StateFlow<Int> = _audioSessionId.asStateFlow()

    private val _isHardwareAttached = MutableStateFlow(false)
    val isHardwareAttached: StateFlow<Boolean> = _isHardwareAttached.asStateFlow()

    // Default min/max in dB (typically +/- 12 dB or +/- 15 dB)
    var minDb: Float = -12.0f
        private set
    var maxDb: Float = 12.0f
        private set

    private var minLevelMb: Short = -1200
    private var maxLevelMb: Short = 1200

    // Preset configurations for 5 standard frequency bands (Low to High)
    private val presetProfiles = mapOf(
        "Jazz" to listOf(3.5f, 2.0f, -1.0f, 2.0f, 4.0f),
        "Rock" to listOf(5.0f, 2.5f, -1.5f, 3.0f, 5.5f),
        "Classical" to listOf(4.0f, 3.0f, 1.0f, 2.0f, -0.5f),
        "Flat" to listOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f),
        "Bass Boost" to listOf(7.0f, 5.0f, 2.0f, 0.0f, 0.0f),
        "Vocal Focus" to listOf(-2.0f, 1.0f, 4.5f, 3.0f, -1.0f)
    )

    val availablePresets: List<String> = listOf("Jazz", "Rock", "Classical", "Flat", "Bass Boost", "Vocal Focus")

    // Default fallback frequencies if hardware bands cannot be queried
    private val fallbackFrequencies = listOf(60, 230, 910, 3600, 14000)

    private val _bands = MutableStateFlow<List<EqualizerBand>>(
        createDefaultBands("Jazz")
    )
    val bands: StateFlow<List<EqualizerBand>> = _bands.asStateFlow()

    private fun createDefaultBands(presetName: String): List<EqualizerBand> {
        val levels = presetProfiles[presetName] ?: List(fallbackFrequencies.size) { 0f }
        return fallbackFrequencies.mapIndexed { index, freq ->
            val label = formatFreq(freq)
            val level = levels.getOrElse(index) { 0f }
            EqualizerBand(index, freq, label, level)
        }
    }

    private fun formatFreq(freqHz: Int): String {
        return if (freqHz >= 1000) {
            val kHz = freqHz / 1000f
            if (kHz % 1f == 0f) "${kHz.toInt()}kHz" else "${String.format("%.1f", kHz)}kHz"
        } else {
            "${freqHz}Hz"
        }
    }

    /**
     * Attach to ExoPlayer's audio session ID
     */
    fun attachAudioSession(sessionId: Int) {
        if (sessionId <= 0) return
        if (currentSessionId == sessionId && equalizer != null) return

        currentSessionId = sessionId
        _audioSessionId.value = sessionId

        try {
            equalizer?.release()
            equalizer = null

            val eq = Equalizer(0, sessionId).apply {
                enabled = _isEnabled.value
            }

            equalizer = eq
            _isHardwareAttached.value = true

            val range = eq.bandLevelRange
            if (range != null && range.size >= 2) {
                minLevelMb = range[0]
                maxLevelMb = range[1]
                minDb = minLevelMb / 100f
                maxDb = maxLevelMb / 100f
            }

            val numBands = eq.numberOfBands.toInt()
            val presetLevels = presetProfiles[_currentPreset.value]

            val newBands = (0 until numBands).map { bandIdx ->
                val centerFreqHz = eq.getCenterFreq(bandIdx.toShort()) / 1000
                val label = formatFreq(centerFreqHz)
                val targetDb = presetLevels?.getOrNull(bandIdx) ?: 0f

                // Apply initial target level to hardware
                val levelMb = (targetDb * 100).roundToInt()
                    .coerceIn(minLevelMb.toInt(), maxLevelMb.toInt()).toShort()
                try {
                    eq.setBandLevel(bandIdx.toShort(), levelMb)
                } catch (e: Exception) {
                    Log.w(tag, "Could not set initial level for band $bandIdx", e)
                }

                EqualizerBand(bandIdx, centerFreqHz, label, targetDb)
            }

            _bands.value = newBands
            Log.d(tag, "Attached Equalizer to audioSessionId: $sessionId with $numBands bands")
        } catch (e: Exception) {
            Log.w(tag, "Could not initialize hardware Equalizer on session $sessionId (running simulation): ${e.message}")
            _isHardwareAttached.value = false
        }
    }

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        try {
            equalizer?.enabled = enabled
        } catch (e: Exception) {
            Log.w(tag, "Error setting equalizer enabled state", e)
        }
    }

    fun applyPreset(presetName: String) {
        _currentPreset.value = presetName
        val targetLevels = presetProfiles[presetName] ?: return

        val updatedBands = _bands.value.mapIndexed { index, band ->
            val level = targetLevels.getOrElse(index) { 0f }
            val levelMb = (level * 100).roundToInt()
                .coerceIn(minLevelMb.toInt(), maxLevelMb.toInt()).toShort()
            try {
                equalizer?.setBandLevel(band.index.toShort(), levelMb)
            } catch (e: Exception) {
                Log.w(tag, "Failed to apply preset level to band ${band.index}", e)
            }
            band.copy(levelDb = level)
        }

        _bands.value = updatedBands
    }

    fun setBandLevel(bandIndex: Int, levelDb: Float) {
        val clampedDb = levelDb.coerceIn(minDb, maxDb)
        val levelMb = (clampedDb * 100).roundToInt()
            .coerceIn(minLevelMb.toInt(), maxLevelMb.toInt()).toShort()

        try {
            equalizer?.setBandLevel(bandIndex.toShort(), levelMb)
        } catch (e: Exception) {
            Log.w(tag, "Failed to set band level for $bandIndex", e)
        }

        _currentPreset.value = "Custom"
        _bands.value = _bands.value.map { band ->
            if (band.index == bandIndex) band.copy(levelDb = clampedDb) else band
        }
    }

    fun resetToFlat() {
        applyPreset("Flat")
    }

    fun release() {
        try {
            equalizer?.release()
            equalizer = null
        } catch (e: Exception) {
            Log.w(tag, "Error releasing Equalizer", e)
        }
        _isHardwareAttached.value = false
    }
}
