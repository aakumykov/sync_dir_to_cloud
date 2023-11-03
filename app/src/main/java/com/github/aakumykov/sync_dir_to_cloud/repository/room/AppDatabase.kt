package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTaskBase


@Database(
    entities = [
        SyncTaskBase::class,
        SyncObject::class,
        CloudAuth::class
    ],
    version = 12,
    autoMigrations = [  ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
    abstract fun getSyncObjectDAO(): SyncObjectDAO
    abstract fun getCloudAuthDAO(): CloudAuthDAO
}