package com.github.aakumykov.sync_dir_to_cloud.bb_new.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Database(
    entities = [ SyncTask::class, CloudAuth::class ],
    version = 1
)
abstract class TestDatabase : RoomDatabase() {
    abstract fun getTestSyncTaskDAO(): TestSyncTaskDAO
    abstract fun getTestCloudAuthDAO(): TestCloudAuthDAO
}