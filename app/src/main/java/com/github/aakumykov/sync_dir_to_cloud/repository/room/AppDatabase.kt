package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.comparison_state.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem.Companion.ITEM_NAME_FILED
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem.Companion.OPERATION_NAME_FILED
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem.Companion.PROGRESS_FIELD
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem.Companion.EXECUTION_ID_FIELD_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem.Companion.OPERATION_STATE_FIELD_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem.Companion.TASK_ID_FIELD_NAME
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
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry.Companion.OLD_TABLE_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry.Companion.TABLE_NAME
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncOperationLoggerDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskBackupDirDAO

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
        SyncOperationLogItem::class,
   ],
    autoMigrations = [
        AutoMigration(from = 56, to = 57, spec = RenameTableFromTaskLogsToSyncTaskLogs::class),
        AutoMigration(from = 57, to = 58), // SyncObjectLogItem.message типа String?
        AutoMigration(from = 58, to = 59, spec = RenameColumnMessageToOperationName::class),
        AutoMigration(from = 59, to = 60), // message типа String?
        AutoMigration(from = 60, to = 61, spec = RenameColumnNameToItemName::class),
        AutoMigration(from = 61, to = 62), // Новое поле errorMessage
        AutoMigration(from = 62, to = 63), // Новое поле "operation_state"
        AutoMigration(from = 63, to = 64, spec = DeleteColumnIsSuccessful::class),
        AutoMigration(from = 64, to = 65), // Новое поле "progress"
        AutoMigration(from = 65, to = 66), // Новое поле "progress_as_part_of_100"
        AutoMigration(from = 66, to = 67, spec = DeleteColumnProgress::class),
        AutoMigration(from = 67, to = 68), // Добавление поля qwerty
        AutoMigration(from = 68, to = 69, spec = RenameColumnFromQwertyToAbc::class),
        AutoMigration(from = 69, to = 70, spec = RenameColumnProgressAsPartOf100ToProgress::class),
        AutoMigration(from = 70, to = 71, spec = DeleteColumnAbc::class),
        AutoMigration(from = 71, to = 72),
        AutoMigration(from = 72, to = 73, spec = RenameColumnFromTimestampToStartTime::class),
        AutoMigration(from = 73, to = 74), // Добавление поля TaskLogEntry.finishTime
        AutoMigration(from = 74, to = 75), // Добавление поля TaskLogEntry.size
        AutoMigration(from = 75, to = 76), // добавилось ExecutionLogItem
        AutoMigration(from = 76, to = 77, spec = RenameColumnsAutoMigrationSpec1::class), // добавилось ExecutionLogItem
        AutoMigration(from = 77, to = 78), // Новое поле "operationState" в ExecutionLogItem
        AutoMigration(from = 78, to = 79, spec = RemoveOperationStateFieldSpec::class), // Удаление поля ExecutionLogItem.operationState
        AutoMigration(from = 79, to = 80), // Новое поле SyncObject.side
        AutoMigration(from = 80, to = 81), // Новое поле SyncObject.executionId
        AutoMigration(from = 81, to = 82, spec = FirstAddThisObjectSpec::class), // Новый объект "SyncInstruction"
        AutoMigration(from = 82, to = 83, spec = DeleteColumnExecutionIdSpec::class),
        AutoMigration(from = 83, to = 84), // Добавил внешний ключ к SyncInstruction.
        AutoMigration(from = 84, to = 85), // Добавил поле SyncInstruction.isDir
        AutoMigration(from = 85, to = 86, spec = RenameSideToSyncSideMigration::class), // SyncObject.side --> syncSide
        AutoMigration(from = 86, to = 87), // Новый объект SyncInstruction5
        AutoMigration(from = 87, to = 88), // Переместил поля в SyncInstruction5
        AutoMigration(from = 88, to = 89), // Новое поле SyncInstruction5.executionOrderNum
        AutoMigration(from = 89, to = 90, spec = SyncInstruction5RenameOrderNumToGroupOrderNumMigrationSpec::class),
        AutoMigration(from = 90, to = 91, spec = SyncInstruction5RenameObjectIdToSourceObjectIdMigrationSpec::class),
        AutoMigration(from = 91, to = 92), // SyncInstruction5.sourceObjectId стало nullable.
        AutoMigration(from = 92, to = 93), // Новое поле SyncInstruction5.executionOrderNum
        AutoMigration(from = 93, to = 94, spec = SyncInstruction5DeleteSyncSideColumnMigrationSpec::class),
        AutoMigration(from = 94, to = 95, spec = SyncInstruction5DeleteIsDirColumnMigrationSpec::class),
        AutoMigration(from = 95, to = 96), // Новое поле SyncTask.withBackup
        AutoMigration(from = 96, to = 97), // ComparisonState
        AutoMigration(from = 97, to = 98), // SyncInstruction6
        AutoMigration(from = 98, to = 99, spec = RenameObjectIdColumnsMigration1::class),
        AutoMigration(from = 99, to = 100), // Новое поле ComparisonState.isDir
        AutoMigration(from = 100, to = 101), // null-able поля в SyncInstruction6
        AutoMigration(from = 101, to = 102), // Новое поле SyncInstruction6.relativePath
        AutoMigration(from = 102, to = 103), // ForeignKeys в SyncInstruction6
        AutoMigration(from = 103, to = 104), // ForeignKeys в SyncInstruction6
        AutoMigration(from = 104, to = 105), // Новое поле SyncInstruction6.orderNum
        AutoMigration(from = 105, to = 106), // Новое поле SyncInstruction6.isDir
        AutoMigration(from = 106, to = 107, spec = RenameStateInSourceToStateInStorageMigration::class),
        AutoMigration(from = 107, to = 108), // Новое поле SyncObject.stateJustDetected
        AutoMigration(from = 108, to = 109, spec = SyncObjectDeleteStateJustDetectedField::class),
        AutoMigration(from = 109, to = 110), // Новое поле SyncObject.justChecked
        AutoMigration(from = 110, to = 111), // Новое поле SyncInstruction6.isProcessed
        AutoMigration(from = 111, to = 112, spec = DeleteTableSyncInstructions5::class),
        AutoMigration(from = 112, to = 113), // Новый объект SyncOperationLogItem
        AutoMigration(from = 113, to = 114), // Новое поле SyncOperationLogItem.errorMsg
        AutoMigration(from = 114, to = 115), // Внешний ключ в SyncOperationLogItem
        AutoMigration(from = 115, to = 116), // Внешний ключ в ExecutionLogItem
        AutoMigration(from = 116, to = 117), // Внешний ключ в TaskLogEntry
        AutoMigration(from = 117, to = 118, spec = DeleteTableSyncInstructions::class), // Удалил SyncInstruction
        AutoMigration(from = 118, to = 119, spec = RenameTableFromSyncInstructions6ToSyncInstructions::class), // Удалил SyncInstruction
        AutoMigration(from = 119, to = 120), // Новые поля SyncTask.sourceBackupDir, targetBackupDir
        AutoMigration(from = 120, to = 121), // Индексы поля task_id в ComparisonState, SyncInstruction, ExecutionLogItem, SyncOperationLogItem, TaskLogEntry.
        AutoMigration(from = 121, to = 122), // Новые поля SyncTask.sourceExecutionBackupDir, targetExecutionBackupDir
        AutoMigration(from = 122, to = 123), // Новое поле SyncInstruction.partsLabel
        AutoMigration(from = 123, to = 124, spec = RenameSyncTaskBackupDirToDirName::class),
        AutoMigration(from = 124, to = 125, spec = RenameSyncTaskBackupDirNameToTaskBackupDirName::class),
        AutoMigration(from = 125, to = 126), // Новое поле ExecutionLogItem.details: String?
        AutoMigration(from = 126, to = 127), // Новое поле SyncOperationLogItem.jobId: String?
        AutoMigration(from = 127, to = 128, spec = RenameTableFromExecutionLogToTaskExecutionLog::class), // Переименование таблицы "execution_log" в "task_execution_log".
    ],
    version = 128,
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
    abstract fun getComparisonStateDAO(): ComparisonStateDAO
    abstract fun getSyncInstructionDAO6(): SyncInstructionDAO
    abstract fun getSyncOperationLoggerDAO(): SyncOperationLoggerDAO
    abstract fun getSyncTaskBackupDirDAO(): SyncTaskBackupDirDAO
}

class FirstAddThisObjectSpec : AutoMigrationSpec

@DeleteColumn(tableName = "sync_instructions", columnName = "execution_id")
class DeleteColumnExecutionIdSpec : AutoMigrationSpec {}


@RenameColumn(tableName = "sync_instructions_5", fromColumnName = "order_num", toColumnName = "execution_order_num")
class SyncInstruction5RenameOrderNumToGroupOrderNumMigrationSpec : AutoMigrationSpec

@RenameColumn(tableName = "sync_instructions_5", fromColumnName = "object_id", toColumnName = "source_object_id")
class SyncInstruction5RenameObjectIdToSourceObjectIdMigrationSpec : AutoMigrationSpec

@DeleteColumn(tableName = "sync_instructions_5", columnName = "sync_side")
class SyncInstruction5DeleteSyncSideColumnMigrationSpec : AutoMigrationSpec

@DeleteColumn(tableName = "sync_instructions_5", columnName = "is_dir")
class SyncInstruction5DeleteIsDirColumnMigrationSpec : AutoMigrationSpec


@DeleteColumn(tableName = "sync_objects", columnName = "state_just_detected")
class SyncObjectDeleteStateJustDetectedField : AutoMigrationSpec


@DeleteTable(tableName = "sync_instructions_5")
class DeleteTableSyncInstructions5 : AutoMigrationSpec

@DeleteTable(tableName = "sync_instructions")
class DeleteTableSyncInstructions : AutoMigrationSpec


@RenameTable(fromTableName = "sync_instructions_6", toTableName = "sync_instructions")
class RenameTableFromSyncInstructions6ToSyncInstructions : AutoMigrationSpec


@RenameColumn(tableName = "sync_tasks", fromColumnName = "source_backup_dir", toColumnName = "source_task_backup_dir_name")
@RenameColumn(tableName = "sync_tasks", fromColumnName = "target_backup_dir", toColumnName = "target_task_backup_dir_name")
@RenameColumn(tableName = "sync_tasks", fromColumnName = "source_execution_backup_dir", toColumnName = "source_execution_backup_dir_name")
@RenameColumn(tableName = "sync_tasks", fromColumnName = "target_execution_backup_dir", toColumnName = "target_execution_backup_dir_name")
class RenameSyncTaskBackupDirToDirName : AutoMigrationSpec


@RenameColumn(tableName = "sync_tasks", fromColumnName = "source_backup_dir_name", toColumnName = "source_task_backup_dir_name")
@RenameColumn(tableName = "sync_tasks", fromColumnName = "target_backup_dir_name", toColumnName = "target_task_backup_dir_name")
class RenameSyncTaskBackupDirNameToTaskBackupDirName : AutoMigrationSpec


@RenameTable(fromTableName = "execution_log", toTableName = "task_execution_log")
class RenameTableFromExecutionLogToTaskExecutionLog : AutoMigrationSpec


@RenameColumn(tableName = "execution_log", fromColumnName = "executionId", toColumnName = EXECUTION_ID_FIELD_NAME)
@RenameColumn(tableName = "execution_log", fromColumnName = "taskId", toColumnName = TASK_ID_FIELD_NAME)
class RenameColumnsAutoMigrationSpec1 : AutoMigrationSpec


@DeleteColumn(tableName = "execution_log", columnName = OPERATION_STATE_FIELD_NAME)
class RemoveOperationStateFieldSpec : AutoMigrationSpec


@RenameTable(fromTableName = OLD_TABLE_NAME, toTableName = TABLE_NAME)
class RenameTableFromTaskLogsToSyncTaskLogs : AutoMigrationSpec


@RenameColumn(tableName = TABLE_NAME, fromColumnName = "timestamp", toColumnName = "start_time")
class RenameColumnFromTimestampToStartTime : AutoMigrationSpec


@RenameColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, fromColumnName = "message", toColumnName = OPERATION_NAME_FILED)
class RenameColumnMessageToOperationName : AutoMigrationSpec

@RenameColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, fromColumnName = "name", toColumnName = ITEM_NAME_FILED)
class RenameColumnNameToItemName : AutoMigrationSpec

@DeleteColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, columnName = "is_successful")
class DeleteColumnIsSuccessful : AutoMigrationSpec

@DeleteColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, columnName = "progress")
class DeleteColumnProgress : AutoMigrationSpec

@RenameColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, fromColumnName = "qwerty", toColumnName = "abc")
class RenameColumnFromQwertyToAbc : AutoMigrationSpec

@RenameColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, fromColumnName = "progress_as_part_of_100", toColumnName = PROGRESS_FIELD)
class RenameColumnProgressAsPartOf100ToProgress : AutoMigrationSpec

@DeleteColumn(tableName = SyncObjectLogItem.Companion.TABLE_NAME, columnName = "abc")
class DeleteColumnAbc : AutoMigrationSpec


@RenameColumn(tableName = "sync_objects", fromColumnName = "side", toColumnName = "sync_side")
class RenameSideToSyncSideMigration : AutoMigrationSpec

@RenameColumn(tableName = "sync_objects", fromColumnName = "state_in_source", toColumnName = "state_in_source")
class RenameStateInSourceToStateInStorageMigration : AutoMigrationSpec


@RenameColumn(
    tableName = "sync_instructions_6",
    fromColumnName = "from_id",
    toColumnName = "object_id_in_source")
@RenameColumn(
    tableName = "sync_instructions_6",
    fromColumnName = "to_id",
    toColumnName = "object_id_in_target")
class RenameObjectIdColumnsMigration1: AutoMigrationSpec


