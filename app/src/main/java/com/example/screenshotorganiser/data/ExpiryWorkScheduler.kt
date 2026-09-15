package com.example.screenshotorganiser.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ExpiryWorkScheduler {

    private const val WORK_NAME = "screenshot_expiry_check"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ExpiryWorker>(
            1,
            TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}