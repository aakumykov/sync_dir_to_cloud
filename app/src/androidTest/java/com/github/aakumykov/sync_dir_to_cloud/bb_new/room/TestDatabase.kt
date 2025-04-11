package com.github.aakumykov.sync_dir_to_cloud.bb_new.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Database(
    entities = [ SyncTask::class ],
    version = 1
)
abstract class TestDatabase : RoomDatabase() {

    abstract fun getTestSyncTaskDAO(): TestSyncTaskDAO
}