package com.example.screenshotorganiser.data

import android.net.Uri

data class Screenshot(
    val uri: Uri,
    val name: String,
    val ocrText: String? = null,
    val category: String? = null
)
