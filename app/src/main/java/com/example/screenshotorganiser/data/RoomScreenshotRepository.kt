package com.example.screenshotorganiser.data

import kotlinx.coroutines.flow.Flow

class RoomScreenshotRepository(
    private val dao: ScreenshotDao
) {

    fun getAllScreenshots(): Flow<List<ScreenshotEntity>> {
        return dao.getAllScreenshots()
    }

    suspend fun getScreenshotById(id: Long): ScreenshotEntity? {
        return dao.getScreenshotById(id)
    }

    suspend fun insert(screenshot: ScreenshotEntity) {
        dao.insert(screenshot)
    }

    suspend fun update(screenshot: ScreenshotEntity) {
        dao.update(screenshot)
    }

    suspend fun delete(screenshot: ScreenshotEntity) {
        dao.delete(screenshot)
    }

    suspend fun getExpiredScreenshots(): List<ScreenshotEntity> {
        return dao.getExpiredScreenshots(System.currentTimeMillis())
    }

    fun searchByOcrText(query: String): Flow<List<ScreenshotEntity>> {
        return dao.searchByOcrText(query)
    }

    fun getReviewRequiredScreenshots(): Flow<List<ScreenshotEntity>> {
        return dao.getReviewRequiredScreenshots()
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}