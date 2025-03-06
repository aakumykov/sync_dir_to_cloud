package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionDAO6
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateSetterDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO5
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO

@Database(
    entities = [
        SyncTask::class,
        SyncObject::class,
        CloudAuth::class,
        TaskLogEntry::class,
        SyncObjectLogItem::class,
        ExecutionLogItem::class,
        SyncInstruction::class,
        SyncInstruction5::class,
        ComparisonState::class,
        SyncInstruction6::class,
   ],
    autoMigrations = [
        AutoMigration(from = 56, to = 57, spec = TaskLogEntry.RenameTableFromTaskLogsToSyncTaskLogs::class),
        AutoMigration(from = 57, to = 58), // SyncObjectLogItem.message типа String?
        AutoMigration(from = 58, to = 59, spec = SyncObjectLogItem.RenameColumnMessageToOperationName::class),
        AutoMigration(from = 59, to = 60), // SyncObjectLogItem.message типа String?
        AutoMigration(from = 60, to = 61, spec = SyncObjectLogItem.RenameColumnNameToItemName::class),
        AutoMigration(from = 61, to = 62), // Новое поле SyncObjectLogItem.errorMessage
        AutoMigration(from = 62, to = 63), // Новое поле "operation_state"
        AutoMigration(from = 63, to = 64, spec = SyncObjectLogItem.DeleteColumnIsSuccessful::class),
        AutoMigration(from = 64, to = 65), // Новое поле "progress"
        AutoMigration(from = 65, to = 66), // Новое поле "progress_as_part_of_100"
        AutoMigration(from = 66, to = 67, spec = SyncObjectLogItem.DeleteColumnProgress::class),
        AutoMigration(from = 67, to = 68), // Добавление поля qwerty
        AutoMigration(from = 68, to = 69, spec = SyncObjectLogItem.RenameColumnFromQwertyToAbc::class),
        AutoMigration(from = 69, to = 70, spec = SyncObjectLogItem.RenameColumnProgressAsPartOf100ToProgress::class),
        AutoMigration(from = 70, to = 71, spec = SyncObjectLogItem.DeleteColumnAbc::class),
        AutoMigration(from = 71, to = 72),
        AutoMigration(from = 72, to = 73, spec = TaskLogEntry.RenameColumnFromTimestampToStartTime::class),
        AutoMigration(from = 73, to = 74), // Добавление поля TaskLogEntry.finishTime
        AutoMigration(from = 74, to = 75), // Добавление поля TaskLogEntry.size
        AutoMigration(from = 75, to = 76), // добавилось ExecutionLogItem
        AutoMigration(from = 76, to = 77, spec = ExecutionLogItem.RenameColumnsAutoMigrationSpec1::class), // добавилось ExecutionLogItem
        AutoMigration(from = 77, to = 78), // Новое поле "operationState" в ExecutionLogItem
        AutoMigration(from = 78, to = 79, spec = ExecutionLogItem.RemoveOperationStateFieldSpec::class), // Удаление поля ExecutionLogItem.operationState
        AutoMigration(from = 79, to = 80), // Новое поле SyncObject.side
        AutoMigration(from = 80, to = 81), // Новое поле SyncObject.executionId
        AutoMigration(from = 81, to = 82, spec = SyncInstruction.FirstAddThisObjectSpec::class), // Новый объект "SyncInstruction"
        AutoMigration(from = 82, to = 83, spec = SyncInstruction.DeleteColumnExecutionIdSpec::class),
        AutoMigration(from = 83, to = 84), // Добавил внешний ключ к SyncInstruction.
        AutoMigration(from = 84, to = 85), // Добавил поле SyncInstruction.isDir
        AutoMigration(from = 85, to = 86, spec = SyncObject.RenameSideToSyncSideMigration::class), // SyncObject.side --> syncSide
        AutoMigration(from = 86, to = 87), // Новый объект SyncInstruction5
        AutoMigration(from = 87, to = 88), // Переместил поля в SyncInstruction5
        AutoMigration(from = 88, to = 89), // Новое поле SyncInstruction5.executionOrderNum
        AutoMigration(from = 89, to = 90, spec = SyncInstruction5.RenameOrderNumToGroupOrderNumMigrationSpec::class),
        AutoMigration(from = 90, to = 91, spec = SyncInstruction5.RenameObjectIdToSourceObjectIdMigrationSpec::class),
        AutoMigration(from = 91, to = 92), // SyncInstruction5.sourceObjectId стало nullable.
        AutoMigration(from = 92, to = 93), // Новое поле SyncInstruction5.executionOrderNum
        AutoMigration(from = 93, to = 94, spec = SyncInstruction5.DeleteSyncSideColumnMigrationSpec::class),
        AutoMigration(from = 94, to = 95, spec = SyncInstruction5.DeleteIsDirColumnMigrationSpec::class),
        AutoMigration(from = 95, to = 96), // Новое поле SyncTask.withBackup
        AutoMigration(from = 96, to = 97), // ComparisonState
        AutoMigration(from = 97, to = 98), // SyncInstruction6
        AutoMigration(from = 98, to = 99, spec = SyncInstruction6.RenameObjectIdColumnsMigration1::class),
        AutoMigration(from = 99, to = 100), // Новое поле ComparisonState.isDir
        AutoMigration(from = 100, to = 101), // null-able поля в SyncInstruction6
    ],
    version = 101,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSyncTaskDAO(): SyncTaskDAO
    abstract fun getSyncObjectDAO(): SyncObjectDAO
    abstract fun getSyncObjectStateDAO(): SyncObjectStateSetterDAO
    abstract fun getSyncObjectResettingDAO(): BadObjectStateResettingDAO
    abstract fun getCloudAuthDAO(): CloudAuthDAO

    abstract fun getSyncTaskStateDAO(): SyncTaskStateDAO
    abstract fun getSyncTaskSchedulingStateDAO(): SyncTaskSchedulingStateDAO
    abstract fun getSyncTaskExecutionStateDAO(): SyncTaskSyncStateDAO
    abstract fun getSyncTaskRunningTimeDAO(): SyncTaskRunningTimeDAO
    abstract fun getSyncObjectBadStateResettingDAO(): SyncObjectBadStateResettingDAO
    abstract fun getSyncTaskResettingDAO(): SyncTaskResettingDAO
    abstract fun getTaskLogDAO(): SyncTaskLogDAO
    abstract fun getSyncObjectLogDAO(): SyncObjectLogDAO
    abstract fun getExecutionLogDAO(): ExecutionLogDAO
    abstract fun getSyncInstructionDAO(): SyncInstructionDAO
    abstract fun getSyncInstructionDAO5(): SyncInstructionDAO5
    abstract fun getComparisonStateDAO(): ComparisonStateDAO
    abstract fun getSyncInstructionDAO6(): SyncInstructionDAO6
}