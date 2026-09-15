package com.example.screenshotorganiser.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshots")
data class ScreenshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val uri: String,
    val name: String,
    val ocrText: String? = null,
    val category: String? = null,
    val expiryDate: Long? = null,
    val isArchived: Boolean = false,
    val isSensitive: Boolean = false
)