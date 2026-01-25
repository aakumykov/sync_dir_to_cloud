package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem.Companion.ITEM_NAME_FILED
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem.Companion.OPERATION_NAME_FILED
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem.Companion.PROGRESS_FIELD
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem.Companion.EXECUTION_ID_FIELD_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem.Companion.OPERATION_STATE_FIELD_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem.Companion.TASK_ID_FIELD_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry.Companion.OLD_TABLE_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry.Companion.TABLE_NAME

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



@RenameTable(fromTableName = "sync_operation_logs", toTableName = "file_operation_logs")
class RenameTableFromSyncOperationLogItemToFileOperationLogItem : AutoMigrationSpec


@RenameColumn(
    tableName = "task_logs",
    fromColumnName = "entry_type",
    toColumnName = "log_item_type"
)
@RenameColumn(
    tableName = "instruction_logs",
    fromColumnName = "entry_type",
    toColumnName = "log_item_type"
)
class RenameLogEntryTypeToLogItemType : AutoMigrationSpec


