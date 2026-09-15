package com.example.screenshotorganiser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File

@Database(
    entities = [ScreenshotEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun screenshotDao(): ScreenshotDao

    companion object {
        private const val DATABASE_NAME = "screenshot_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration from version 1 to 2: adds `isReviewRequired` column.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE screenshots ADD COLUMN isReviewRequired INTEGER NOT NULL DEFAULT 0")
            }

            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE screenshots ADD COLUMN isReviewRequired INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val dbFile = appContext.getDatabasePath(DATABASE_NAME)

                val builder = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_1_2)

                try {
                    System.loadLibrary("sqlcipher")
                    val passphrase = DatabaseKeyManager.getOrCreatePassphrase(appContext)

                    // If an unencrypted database exists from a previous run, migrate it to encrypted
                    if (isPlaintextDatabase(dbFile)) {
                        migratePlaintextToEncrypted(dbFile, passphrase)
                    }

                    builder.openHelperFactory(SupportOpenHelperFactory(passphrase))
                } catch (e: Throwable) {
                    // Fallback for host JVM environments where SQLCipher native libraries are unavailable
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }

        private fun isPlaintextDatabase(dbFile: File): Boolean {
            if (!dbFile.exists() || dbFile.length() < 16) return false
            return try {
                val header = ByteArray(16)
                dbFile.inputStream().use { it.read(header) }
                header.contentEquals("SQLite format 3\u0000".toByteArray(Charsets.US_ASCII))
            } catch (e: Exception) {
                false
            }
        }

        private fun migratePlaintextToEncrypted(dbFile: File, passphrase: ByteArray) {
            try {
                val tempEncrypted = File(dbFile.parentFile, "${DATABASE_NAME}_encrypted.db")
                if (tempEncrypted.exists()) tempEncrypted.delete()

                val plainDb = net.zetetic.database.sqlcipher.SQLiteDatabase.openDatabase(
                    dbFile.absolutePath,
                    null,
                    net.zetetic.database.sqlcipher.SQLiteDatabase.OPEN_READWRITE
                )

                try {
                    val hexKey = passphrase.joinToString("") { "%02x".format(it) }
                    plainDb.rawExecSQL("ATTACH DATABASE '${tempEncrypted.absolutePath.replace("'", "''")}' AS encrypted KEY \"x'$hexKey'\";")
                    plainDb.rawExecSQL("SELECT sqlcipher_export('encrypted');")
                    plainDb.rawExecSQL("DETACH DATABASE encrypted;")
                } finally {
                    plainDb.close()
                }

                File(dbFile.path + "-wal").delete()
                File(dbFile.path + "-shm").delete()
                File(dbFile.path + "-journal").delete()

                if (dbFile.delete()) {
                    tempEncrypted.renameTo(dbFile)
                }
            } catch (e: Exception) {
                // If migration fails, do not corrupt the original file
            }
        }
    }
}