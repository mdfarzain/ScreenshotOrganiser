package com.example.screenshotorganiser.classification

data class ClassificationResult(
    val category: Category,
    val confidence: Float,
    val ocrText: String,
    val detectedDates: List<String> = emptyList(),
    val detectedPhoneNumbers: List<String> = emptyList(),
    val detectedEmails: List<String> = emptyList(),
    val detectedCurrency: List<String> = emptyList()
)