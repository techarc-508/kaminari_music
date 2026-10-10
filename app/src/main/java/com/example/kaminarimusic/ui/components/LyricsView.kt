package com.example.kaminarimusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class LyricLine(val timeMs: Long, val text: String)

fun parseLrc(lrcText: String?): List<LyricLine> {
    if (lrcText.isNullOrBlank()) return emptyList()
    val lines = lrcText.split("\n")
    val result = mutableListOf<LyricLine>()
    val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]""")
    val regexSimple = Regex("""\[(\d{2}):(\d{2})]""")

    for (line in lines) {
        val match = regex.find(line)
        if (match != null) {
            val min = match.groupValues[1].toLong()
            val sec = match.groupValues[2].toLong()
            val millis = match.groupValues[3].toLong() * 10
            val timeMs = min * 60000 + sec * 1000 + millis
            val text = line.replace(regex, "").trim()
            if (text.isNotEmpty()) {
                result.add(LyricLine(timeMs, text))
            }
        } else {
            val matchSimple = regexSimple.find(line)
            if (matchSimple != null) {
                val min = matchSimple.groupValues[1].toLong()
                val sec = matchSimple.groupValues[2].toLong()
                val timeMs = min * 60000 + sec * 1000
                val text = line.replace(regexSimple, "").trim()
                if (text.isNotEmpty()) {
                    result.add(LyricLine(timeMs, text))
                }
            }
        }
    }
    return result.sortedBy { it.timeMs }
}

@Composable
fun LyricsView(
    lyricsText: String?,
    currentPositionMs: Long
) {
    val lyricLines = remember(lyricsText) { parseLrc(lyricsText) }
    val listState = rememberLazyListState()

    val currentIndex = remember(currentPositionMs, lyricLines) {
        var index = lyricLines.indexOfLast { it.timeMs <= currentPositionMs }
        if (index < 0) index = 0
        index
    }

    LaunchedEffect(currentIndex) {
        if (lyricLines.isNotEmpty()) {
            listState.animateScrollToItem(maxOf(0, currentIndex - 2))
        }
    }

    if (lyricLines.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = lyricsText ?: "No synchronized lyrics available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().height(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(lyricLines) { index, line ->
            val isCurrent = index == currentIndex
            Text(
                text = line.text,
                style = if (isCurrent) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
