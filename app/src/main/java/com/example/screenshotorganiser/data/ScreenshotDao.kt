package com.example.screenshotorganiser.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenshotDao {

    @Insert
    suspend fun insert(screenshot: ScreenshotEntity)

    @Update
    suspend fun update(screenshot: ScreenshotEntity)

    @Delete
    suspend fun delete(screenshot: ScreenshotEntity)

    @Query("SELECT * FROM screenshots")
    fun getAllScreenshots(): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots WHERE id = :id")
    suspend fun getScreenshotById(id: Long): ScreenshotEntity?

    @Query("SELECT * FROM screenshots WHERE expiryDate IS NOT NULL AND expiryDate <= :currentTime")
    suspend fun getExpiredScreenshots(currentTime: Long): List<ScreenshotEntity>

    @Query("DELETE FROM screenshots")
    suspend fun deleteAll()
}