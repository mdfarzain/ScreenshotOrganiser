package com.example.screenshotorganiser.data

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenshotRepository(private val context: Context) {

    suspend fun getScreenshots(): List<Screenshot> = withContext(Dispatchers.IO) {
        val screenshots = mutableListOf<Screenshot>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        // Query the Pictures/Screenshots folder or Screenshots bucket
        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ? OR ${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        } else {
            @Suppress("DEPRECATION")
            "${MediaStore.Images.Media.DATA} LIKE ? OR ${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        }
        val selectionArgs = arrayOf("%Screenshots%", "Screenshots")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn) ?: "Screenshot_$id"
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    screenshots.add(
                        Screenshot(
                            uri = contentUri,
                            name = name
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        screenshots
    }
}
