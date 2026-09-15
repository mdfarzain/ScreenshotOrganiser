package com.example.screenshotorganiser.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpiryReviewTest {

    private val now = 1774000000000L

    @Test
    fun expiredScreenshot_flaggedForReview_notDeletedAndNotArchived() {
        val expiredScreenshot = ScreenshotEntity(
            id = 1,
            uri = "content://screenshots/1",
            name = "Event Ticket",
            ocrText = "Concert Ticket 2026",
            category = "EVENT",
            expiryDate = now - 10000L, // in the past
            isArchived = false,
            isSensitive = false,
            isReviewRequired = false
        )

        assertTrue(ExpiryEngine.isExpired(expiredScreenshot.expiryDate, now))

        // Expiry review processing: flag for user review
        val flaggedScreenshot = if (ExpiryEngine.isExpired(expiredScreenshot.expiryDate, now)) {
            expiredScreenshot.copy(isReviewRequired = true)
        } else {
            expiredScreenshot
        }

        assertTrue(flaggedScreenshot.isReviewRequired)
        assertFalse("Expired screenshot must not be archived", flaggedScreenshot.isArchived)
        assertEquals(expiredScreenshot.id, flaggedScreenshot.id)
        assertEquals(expiredScreenshot.uri, flaggedScreenshot.uri)
    }

    @Test
    fun nonExpiredScreenshot_remainsUnflagged() {
        val activeScreenshot = ScreenshotEntity(
            id = 2,
            uri = "content://screenshots/2",
            name = "Future Event",
            ocrText = "Conference November 2026",
            category = "EVENT",
            expiryDate = now + 1000000L, // in the future
            isArchived = false,
            isSensitive = false,
            isReviewRequired = false
        )

        assertFalse(ExpiryEngine.isExpired(activeScreenshot.expiryDate, now))

        val result = if (ExpiryEngine.isExpired(activeScreenshot.expiryDate, now)) {
            activeScreenshot.copy(isReviewRequired = true)
        } else {
            activeScreenshot
        }

        assertFalse(result.isReviewRequired)
    }

    @Test
    fun contactAndGeneralScreenshots_neverExpireOrRequireReview() {
        val contactExpiry = ExpiryEngine.calculateExpiryDate("CONTACT", now)
        val generalExpiry = ExpiryEngine.calculateExpiryDate("GENERAL", now)

        val contactScreenshot = ScreenshotEntity(
            id = 3,
            uri = "content://screenshots/3",
            name = "Contact Card",
            ocrText = "Alex +1234567890",
            category = "CONTACT",
            expiryDate = contactExpiry,
            isReviewRequired = false
        )

        val generalScreenshot = ScreenshotEntity(
            id = 4,
            uri = "content://screenshots/4",
            name = "General Note",
            ocrText = "A note to remember",
            category = "GENERAL",
            expiryDate = generalExpiry,
            isReviewRequired = false
        )

        assertFalse(ExpiryEngine.isExpired(contactScreenshot.expiryDate, now))
        assertFalse(ExpiryEngine.isExpired(generalScreenshot.expiryDate, now))
    }

    @Test
    fun alreadyFlaggedScreenshot_avoidsUnnecessaryModification() {
        val alreadyFlagged = ScreenshotEntity(
            id = 5,
            uri = "content://screenshots/5",
            name = "Old Receipt",
            ocrText = "Receipt #456",
            category = "RECEIPT",
            expiryDate = now - 50000L,
            isArchived = false,
            isReviewRequired = true
        )

        // Verifying the condition used in ExpiryWorker: only update if !screenshot.isReviewRequired
        val needsUpdate = ExpiryEngine.isExpired(alreadyFlagged.expiryDate, now) && !alreadyFlagged.isReviewRequired
        assertFalse("Already flagged screenshots should not trigger redundant updates", needsUpdate)
    }
}
