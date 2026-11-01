package com.example.util

import android.content.Context
import androidx.work.*
import com.example.workers.DriveBackupWorker
import java.util.concurrent.TimeUnit

object SyncManager {

    fun scheduleBackup(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<DriveBackupWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DriveBackup",
            ExistingPeriodicWorkPolicy.UPDATE,
            backupRequest
        )
    }

    fun scheduleFeedSync(context: Context) {
        // Placeholder for future feed sync worker
    }
}
