package com.example.kaminarimusic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val operationId: Long = 0L,
    val entityId: String,
    val serverId: String? = null,
    val type: String, // UPDATE_METADATA, UPDATE_ARTWORK, DELETE, UPLOAD, REPLACE
    val status: String, // PENDING, PROCESSING, SYNCED, FAILED
    val payloadJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val errorMessage: String? = null
)
