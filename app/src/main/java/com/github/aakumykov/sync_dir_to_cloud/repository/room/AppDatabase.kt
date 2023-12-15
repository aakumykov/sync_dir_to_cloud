package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


@Database(
    entities = [ SyncTask::class, SyncObject::class, CloudAuth::class ],
    version = 17,
    autoMigrations = []
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
    abstract fun getSyncTaskStateDAO(): SyncTaskStateDAO
    abstract fun getSyncObjectDAO(): SyncObjectDAO
    abstract fun getCloudAuthDAO(): CloudAuthDAO
}