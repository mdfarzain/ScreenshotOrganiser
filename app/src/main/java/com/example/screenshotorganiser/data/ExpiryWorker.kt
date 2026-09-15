package com.example.screenshotorganiser.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ExpiryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = RoomScreenshotRepository(database.screenshotDao())

            val expiredScreenshots = repository.getExpiredScreenshots()

            expiredScreenshots.forEach { screenshot ->
                if (!screenshot.isReviewRequired) {
                    repository.update(
                        screenshot.copy(isReviewRequired = true)
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}