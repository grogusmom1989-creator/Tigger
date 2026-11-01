package com.example.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.util.Log
import com.example.util.GoogleDriveManager

class DriveBackupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DriveBackupWorker", "Starting Drive backup sync...")
        
        val dbFile = applicationContext.getDatabasePath("kiki-database")
        if (!dbFile.exists()) {
            Log.e("DriveBackupWorker", "Database file not found")
            return Result.failure()
        }

        try {
            val bytes = dbFile.readBytes()
            val uploadedFileId = GoogleDriveManager.uploadFile("kiki-database.db", bytes)
            if (uploadedFileId != null) {
                Log.d("DriveBackupWorker", "Backup uploaded successfully: $uploadedFileId")
                return Result.success()
            } else {
                Log.e("DriveBackupWorker", "Failed to upload backup")
                return Result.retry()
            }
        } catch (e: Exception) {
            Log.e("DriveBackupWorker", "Error during backup", e)
            return Result.failure()
        }
    }
}
