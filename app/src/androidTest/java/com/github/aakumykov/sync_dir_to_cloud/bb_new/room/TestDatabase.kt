package com.github.aakumykov.sync_dir_to_cloud.bb_new.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
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
        ExecutionLogItem::class,
        SyncInstruction::class,
        ComparisonState::class,
        SyncInstruction6::class,
        SyncOperationLogItem::class,
    ],
    version = 1
)
abstract class TestDatabase : AppDatabase() {
    abstract fun getTestSyncTaskDAO(): TestSyncTaskDAO
    abstract fun getTestCloudAuthDAO(): TestCloudAuthDAO
}