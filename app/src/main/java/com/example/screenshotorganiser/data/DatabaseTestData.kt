package com.example.screenshotorganiser.data

object DatabaseTestData {

    fun sampleScreenshots(): List<ScreenshotEntity> {
        return listOf(
            ScreenshotEntity(
                uri = "content://test/screenshot1",
                name = "College Event",
                ocrText = "College cultural event on 20 September 2026",
                category = "EVENT",
                expiryDate = null,
                isArchived = false,
                isSensitive = false
            ),
            ScreenshotEntity(
                uri = "content://test/screenshot2",
                name = "Shopping Receipt",
                ocrText = "Receipt Total ₹850",
                category = "RECEIPT",
                expiryDate = null,
                isArchived = false,
                isSensitive = false
            ),
            ScreenshotEntity(
                uri = "content://test/screenshot3",
                name = "Contact Details",
                ocrText = "John 9876543210 john@example.com",
                category = "CONTACT",
                expiryDate = null,
                isArchived = false,
                isSensitive = false
            ),
            ScreenshotEntity(
                uri = "content://test/screenshot4",
                name = "General Screenshot",
                ocrText = "Random screenshot",
                category = "GENERAL",
                expiryDate = null,
                isArchived = false,
                isSensitive = false
            )
        )
    }
}