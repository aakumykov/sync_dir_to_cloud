package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.entities.CloudAuth
import com.github.aakumykov.entities.SyncObject
import com.github.aakumykov.entities.SyncTask


@Database(
    entities = [ SyncTask::class, SyncObject::class, CloudAuth::class ],
    version = 15,
    autoMigrations = []
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
    abstract fun getSyncObjectDAO(): SyncObjectDAO
    abstract fun getCloudAuthDAO(): CloudAuthDAO
}