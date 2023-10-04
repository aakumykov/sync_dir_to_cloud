package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


@Database(
    entities = [ SyncTask::class ],
    version = 3,
    autoMigrations = []
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
}