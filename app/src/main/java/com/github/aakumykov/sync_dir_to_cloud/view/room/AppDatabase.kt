package com.github.aakumykov.sync_dir_to_cloud.view.room

import androidx.room.Database
import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask

@Database(
    entities = [ SyncTask::class ],
    version = 1,
    autoMigrations = []
)
abstract class AppDatabase {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
}