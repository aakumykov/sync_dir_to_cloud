package com.github.aakumykov.sync_dir_to_cloud.bb_new.room

import androidx.room.Database
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestCloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Database(
    entities = [
        SyncTask::class,
        SyncObject::class,
        CloudAuth::class,
        ComparisonState::class,
        FileInstruction::class,
        TaskLogItem::class,
        InstructionLogItem::class,
        FileOperationLogItem::class,
    ],
    views = [
        LogOfSync::class
    ],
    version = 1
)
abstract class TestDatabase : AppDatabase() {
    abstract fun getTestSyncTaskDAO(): TestSyncTaskDAO
    abstract fun getTestCloudAuthDAO(): TestCloudAuthDAO
    abstract fun getTestSyncObjectDAO(): TestSyncObjectDAO
    abstract fun getTestSyncInstructionDAO(): TestSyncInstructionDAO
}