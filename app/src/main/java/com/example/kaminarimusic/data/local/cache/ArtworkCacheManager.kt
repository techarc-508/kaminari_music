package com.example.kaminarimusic.data.local.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ArtworkCacheManager(private val context: Context) {

    private val tag = "ArtworkCacheManager"

    val cacheDir: File by lazy {
        File(context.filesDir, "album_art_cache").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Generates and stores a high-resolution offline album art image locally.
     * Returns the absolute path of the cached file.
     */
    suspend fun generateAndCacheArtwork(
        title: String,
        artist: String,
        key: String
    ): String = withContext(Dispatchers.IO) {
        val safeKey = md5("$title-$artist-$key")
        val file = File(cacheDir, "art_$safeKey.png")

        if (file.exists() && file.length() > 0) {
            return@withContext file.absolutePath
        }

        try {
            val size = 512
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Select color pairs deterministically based on title/artist
            val hash = (title.hashCode() xor artist.hashCode()).let { if (it < 0) -it else it }
            val colorPairs = listOf(
                Pair(0xFF5C4BEA.toInt(), 0xFF1E1E38.toInt()),
                Pair(0xFF6366F1.toInt(), 0xFFEC4899.toInt()),
                Pair(0xFF3B82F6.toInt(), 0xFF8B5CF6.toInt()),
                Pair(0xFF8B5CF6.toInt(), 0xFFF43F5E.toInt()),
                Pair(0xFF06B6D4.toInt(), 0xFF3B82F6.toInt()),
                Pair(0xFF10B981.toInt(), 0xFF3B82F6.toInt())
            )
            val selected = colorPairs[hash % colorPairs.size]

            // Draw modern diagonal gradient
            val paint = Paint().apply {
                isAntiAlias = true
                shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    selected.first, selected.second,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

            // Draw subtle ambient dark radial overlay
            val overlayPaint = Paint().apply {
                isAntiAlias = true
                color = 0x44000000
            }
            canvas.drawCircle(size / 2f, size / 2f, size * 0.45f, overlayPaint)

            // Draw center music badge
            val badgePaint = Paint().apply {
                isAntiAlias = true
                color = 0x33FFFFFF
            }
            canvas.drawCircle(size / 2f, size / 2f, 90f, badgePaint)

            // Write to file
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }
            bitmap.recycle()

            file.absolutePath
        } catch (e: Exception) {
            Log.e(tag, "Failed to cache artwork for $title by $artist", e)
            ""
        }
    }

    fun getCachedFile(localPath: String?): File? {
        if (localPath.isNullOrEmpty()) return null
        val file = File(localPath)
        return if (file.exists() && file.isFile) file else null
    }

    fun getCacheSizeBytes(): Long {
        return try {
            cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    fun getFormattedCacheSize(): String {
        val bytes = getCacheSizeBytes()
        return when {
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024f * 1024f))
            bytes >= 1024 -> String.format("%.1f KB", bytes / 1024f)
            else -> "$bytes B"
        }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        try {
            cacheDir.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.e(tag, "Failed to clear artwork cache", e)
        }
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
