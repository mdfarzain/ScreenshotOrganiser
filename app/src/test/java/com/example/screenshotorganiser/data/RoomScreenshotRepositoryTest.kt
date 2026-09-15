package com.example.screenshotorganiser.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoomScreenshotRepositoryTest {

    private lateinit var fakeDao: FakeScreenshotDao
    private lateinit var repository: RoomScreenshotRepository

    @Before
    fun setUp() {
        fakeDao = FakeScreenshotDao()
        repository = RoomScreenshotRepository(fakeDao)
    }

    @Test
    fun insertAndGetById_retrievesInsertedScreenshot() = runBlocking {
        val screenshot = ScreenshotEntity(
            id = 1,
            uri = "content://test/1",
            name = "Test 1",
            ocrText = "Sample OCR"
        )
        repository.insert(screenshot)

        val retrieved = repository.getScreenshotById(1)
        assertNotNull(retrieved)
        assertEquals("Test 1", retrieved?.name)
        assertEquals("Sample OCR", retrieved?.ocrText)
    }

    @Test
    fun getAllScreenshots_emitsAllScreenshots() = runBlocking {
        repository.insert(ScreenshotEntity(id = 1, uri = "content://test/1", name = "A"))
        repository.insert(ScreenshotEntity(id = 2, uri = "content://test/2", name = "B"))

        val all = repository.getAllScreenshots().first()
        assertEquals(2, all.size)
    }

    @Test
    fun update_modifiesExistingScreenshot() = runBlocking {
        val screenshot = ScreenshotEntity(id = 1, uri = "content://test/1", name = "Original", isReviewRequired = false)
        repository.insert(screenshot)

        repository.update(screenshot.copy(name = "Updated", isReviewRequired = true))

        val updated = repository.getScreenshotById(1)
        assertEquals("Updated", updated?.name)
        assertTrue(updated?.isReviewRequired == true)
    }

    @Test
    fun delete_removesScreenshot() = runBlocking {
        val screenshot = ScreenshotEntity(id = 1, uri = "content://test/1", name = "To Delete")
        repository.insert(screenshot)

        repository.delete(screenshot)

        val retrieved = repository.getScreenshotById(1)
        assertNull(retrieved)
    }

    @Test
    fun deleteAll_clearsAllScreenshots() = runBlocking {
        repository.insert(ScreenshotEntity(id = 1, uri = "content://test/1", name = "1"))
        repository.insert(ScreenshotEntity(id = 2, uri = "content://test/2", name = "2"))

        repository.deleteAll()

        val all = repository.getAllScreenshots().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun searchByOcrText_filtersMatchingScreenshots() = runBlocking {
        repository.insert(
            ScreenshotEntity(id = 1, uri = "content://test/1", name = "Receipt", ocrText = "Payment received $50")
        )
        repository.insert(
            ScreenshotEntity(id = 2, uri = "content://test/2", name = "Ticket", ocrText = "Movie admit one")
        )

        val results = repository.searchByOcrText("Payment").first()
        assertEquals(1, results.size)
        assertEquals(1L, results[0].id)
    }

    @Test
    fun getReviewRequiredScreenshots_emitsOnlyFlaggedScreenshots() = runBlocking {
        repository.insert(
            ScreenshotEntity(id = 1, uri = "content://test/1", name = "Normal", isReviewRequired = false)
        )
        repository.insert(
            ScreenshotEntity(id = 2, uri = "content://test/2", name = "Expired Review", isReviewRequired = true)
        )

        val reviewItems = repository.getReviewRequiredScreenshots().first()
        assertEquals(1, reviewItems.size)
        assertEquals(2L, reviewItems[0].id)
    }

    @Test
    fun getExpiredScreenshots_returnsOnlyPastExpiryScreenshots() = runBlocking {
        val now = System.currentTimeMillis()
        repository.insert(
            ScreenshotEntity(id = 1, uri = "content://test/1", name = "Past", expiryDate = now - 5000)
        )
        repository.insert(
            ScreenshotEntity(id = 2, uri = "content://test/2", name = "Future", expiryDate = now + 50000)
        )
        repository.insert(
            ScreenshotEntity(id = 3, uri = "content://test/3", name = "NoExpiry", expiryDate = null)
        )

        val expired = repository.getExpiredScreenshots()
        assertEquals(1, expired.size)
        assertEquals(1L, expired[0].id)
    }

    /**
     * In-memory test double implementing ScreenshotDao.
     */
    private class FakeScreenshotDao : ScreenshotDao {
        private val items = mutableMapOf<Long, ScreenshotEntity>()
        private val stateFlow = MutableStateFlow<List<ScreenshotEntity>>(emptyList())

        private fun notifyState() {
            stateFlow.value = items.values.toList()
        }

        override suspend fun insert(screenshot: ScreenshotEntity) {
            val key = if (screenshot.id == 0L) (items.keys.maxOrNull() ?: 0L) + 1 else screenshot.id
            items[key] = screenshot.copy(id = key)
            notifyState()
        }

        override suspend fun update(screenshot: ScreenshotEntity) {
            items[screenshot.id] = screenshot
            notifyState()
        }

        override suspend fun delete(screenshot: ScreenshotEntity) {
            items.remove(screenshot.id)
            notifyState()
        }

        override fun getAllScreenshots(): Flow<List<ScreenshotEntity>> = stateFlow

        override suspend fun getScreenshotById(id: Long): ScreenshotEntity? = items[id]

        override suspend fun getExpiredScreenshots(currentTime: Long): List<ScreenshotEntity> {
            return items.values.filter { it.expiryDate != null && it.expiryDate <= currentTime }
        }

        override fun searchByOcrText(query: String): Flow<List<ScreenshotEntity>> {
            return stateFlow.map { list ->
                list.filter { it.ocrText?.contains(query, ignoreCase = true) == true }
            }
        }

        override fun getReviewRequiredScreenshots(): Flow<List<ScreenshotEntity>> {
            return stateFlow.map { list ->
                list.filter { it.isReviewRequired }
            }
        }

        override suspend fun deleteAll() {
            items.clear()
            notifyState()
        }
    }
}
