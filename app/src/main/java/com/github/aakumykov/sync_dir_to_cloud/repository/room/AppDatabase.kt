package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


@Database(
    entities = [ SyncTask::class, SyncObject::class, CloudAuth::class ],
    version = 43,
    autoMigrations = [

    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
    abstract fun getSyncObjectDAO(): SyncObjectDAO
    abstract fun getSyncObjectResettingDAO(): BadObjectStateResettingDAO
    abstract fun getCloudAuthDAO(): CloudAuthDAO

    abstract fun getSyncTaskStateDAO(): SyncTaskStateDAO
    abstract fun getSyncTaskSchedulingStateDAO(): SyncTaskSchedulingStateDAO
    abstract fun getSyncTaskExecutionStateDAO(): SyncTaskSyncStateDAO
    abstract fun getSyncTaskRunningTimeDAO(): SyncTaskRunningTimeDAO
}