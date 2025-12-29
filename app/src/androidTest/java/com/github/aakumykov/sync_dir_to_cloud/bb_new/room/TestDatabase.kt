package com.github.aakumykov.sync_dir_to_cloud.bb_new.room

import androidx.room.Database
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestCloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase

@Database(
    entities = [
        SyncTask::class,
        SyncObject::class,
        CloudAuth::class,
        TaskLogEntry::class,
        SyncObjectLogItem::class,
        TaskExecutionLogItem::class,
        ComparisonState::class,
        SyncInstruction::class,
        FileOperationLogItem::class,
    ],
    version = 1
)
abstract class TestDatabase : AppDatabase() {
    abstract fun getTestSyncTaskDAO(): TestSyncTaskDAO
    abstract fun getTestCloudAuthDAO(): TestCloudAuthDAO
    abstract fun getTestSyncObjectDAO(): TestSyncObjectDAO
    abstract fun getTestSyncInstructionDAO(): TestSyncInstructionDAO
}