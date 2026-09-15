package com.example.screenshotorganiser.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFunctionalityTest {

    private val sampleData = listOf(
        ScreenshotEntity(
            id = 1,
            uri = "content://screenshots/1",
            name = "College Event",
            ocrText = "College cultural event on 20 September 2026",
            category = "EVENT"
        ),
        ScreenshotEntity(
            id = 2,
            uri = "content://screenshots/2",
            name = "Shopping Receipt",
            ocrText = "Receipt Total ₹850",
            category = "RECEIPT"
        ),
        ScreenshotEntity(
            id = 3,
            uri = "content://screenshots/3",
            name = "Contact Card",
            ocrText = "John Doe 9876543210 john@example.com",
            category = "CONTACT"
        ),
        ScreenshotEntity(
            id = 4,
            uri = "content://screenshots/4",
            name = "No OCR Screenshot",
            ocrText = null,
            category = "GENERAL"
        )
    )

    private fun simulateOcrSearch(query: String): List<ScreenshotEntity> {
        return sampleData.filter { it.ocrText?.contains(query, ignoreCase = true) == true }
    }

    @Test
    fun searchByOcrText_matchesExactSubstring() {
        val results = simulateOcrSearch("Receipt Total")
        assertEquals(1, results.size)
        assertEquals(2L, results[0].id)
    }

    @Test
    fun searchByOcrText_caseInsensitiveMatch() {
        val resultsLower = simulateOcrSearch("cultural event")
        val resultsUpper = simulateOcrSearch("CULTURAL EVENT")
        assertEquals(1, resultsLower.size)
        assertEquals(1, resultsUpper.size)
        assertEquals(1L, resultsLower[0].id)
        assertEquals(1L, resultsUpper[0].id)
    }

    @Test
    fun searchByOcrText_matchesPartialWord() {
        val results = simulateOcrSearch("Sept")
        assertEquals(1, results.size)
        assertEquals("College Event", results[0].name)
    }

    @Test
    fun searchByOcrText_nullOcrText_doesNotMatchOrThrow() {
        val results = simulateOcrSearch("General")
        assertTrue(results.none { it.id == 4L })
    }

    @Test
    fun searchByOcrText_noMatches_returnsEmptyList() {
        val results = simulateOcrSearch("NonExistentTextxyz")
        assertTrue(results.isEmpty())
    }
}
