package com.github.aakumykov.sync_dir_to_cloud.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


@Database(
    entities = [ SyncTask::class ],
    version = 2,
    autoMigrations = []
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
}