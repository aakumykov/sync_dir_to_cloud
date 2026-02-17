package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec

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


@RenameColumn(tableName = "execution_log", fromColumnName = "executionId", toColumnName = "execution_id")
@RenameColumn(tableName = "execution_log", fromColumnName = "taskId", toColumnName = "task_id")
class RenameColumnsAutoMigrationSpec1 : AutoMigrationSpec


@DeleteColumn(tableName = "execution_log", columnName = "operation_state")
class RemoveOperationStateFieldSpec : AutoMigrationSpec


@RenameTable(fromTableName = "task_logs", toTableName = "sync_task_logs")
class RenameTableFromTaskLogsToSyncTaskLogs : AutoMigrationSpec


@RenameColumn(tableName = "sync_task_logs", fromColumnName = "timestamp", toColumnName = "start_time")
class RenameColumnFromTimestampToStartTime : AutoMigrationSpec


@RenameColumn(tableName = "sync_object_logs", fromColumnName = "message", toColumnName = "operation_name")
class RenameColumnMessageToOperationName : AutoMigrationSpec

@RenameColumn(tableName = "sync_object_logs", fromColumnName = "name", toColumnName = "item_name")
class RenameColumnNameToItemName : AutoMigrationSpec

@DeleteColumn(tableName = "sync_object_logs", columnName = "is_successful")
class DeleteColumnIsSuccessful : AutoMigrationSpec

@DeleteColumn(tableName = "sync_object_logs", columnName = "progress")
class DeleteColumnProgress : AutoMigrationSpec

@RenameColumn(tableName = "sync_object_logs", fromColumnName = "qwerty", toColumnName = "abc")
class RenameColumnFromQwertyToAbc : AutoMigrationSpec

@RenameColumn(tableName = "sync_object_logs", fromColumnName = "progress_as_part_of_100", toColumnName = "progress")
class RenameColumnProgressAsPartOf100ToProgress : AutoMigrationSpec

@DeleteColumn(tableName = "sync_object_logs", columnName = "abc")
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
    tableName = "instruction_logs",
    fromColumnName = "entry_type",
    toColumnName = "log_item_type"
)
@RenameColumn(
    tableName = "task_logs",
    fromColumnName = "entry_type",
    toColumnName = "log_item_type"
)
class RenameLogEntryTypeToLogItemType : AutoMigrationSpec


@DeleteColumn(
    tableName = "file_operation_logs_2",
    columnName = "file_path"
)
class DeleteFilePathFromFileOperationLogItem2 : AutoMigrationSpec


@DeleteColumn(
    tableName = "file_operation_logs_2",
    columnName = "description"
)
class FileOperationLogItem2SourceItemTargetItem : AutoMigrationSpec


@RenameColumn(
    tableName = "file_operation_logs_2",
    fromColumnName = "firstItem",
    toColumnName = "first_item"
)
@RenameColumn(
    tableName = "file_operation_logs_2",
    fromColumnName = "secondItem",
    toColumnName = "second_item"
)
class FileOperationLogItem2RenameFirstSecondItems : AutoMigrationSpec


@DeleteTable(tableName = "file_operation_logs")
class FileOperationLogItemDeletion : AutoMigrationSpec


@DeleteTable(tableName = "sync_object_logs")
class SyncObjectLogItemDeletion : AutoMigrationSpec


@DeleteTable(tableName = "task_execution_log")
class TaskExecutionLogItemDeletion : AutoMigrationSpec


@RenameTable(fromTableName = "file_operation_logs_2", toTableName = "file_operation_logs")
class RenameFileOperationLogItem2ToFileOperationLogItem : AutoMigrationSpec


@RenameColumn(
    tableName = "sync_logs",
    fromColumnName = "orig_log_id",
    toColumnName = "log_id"
)
class LogOfSyncAssLogItemAboutRenameOrigLogId : AutoMigrationSpec


@RenameColumn(
    tableName = "task_logs",
    fromColumnName = "message",
    toColumnName = "text"
)
@RenameColumn(
    tableName = "instruction_logs",
    fromColumnName = "message",
    toColumnName = "text"
)
@RenameColumn(
    tableName = "file_operation_logs",
    fromColumnName = "message",
    toColumnName = "text"
)
class RenameMessageToText : AutoMigrationSpec


@RenameTable(
    fromTableName = "sync_instructions",
    toTableName = "file_instructions"
)
class RenameSyncInstructionsToFileInstructions : AutoMigrationSpec


@RenameColumn(
    tableName = "task_logs",
    fromColumnName = "timestamp",
    toColumnName = "start_time"
)
@RenameColumn(
    tableName = "instruction_logs",
    fromColumnName = "timestamp",
    toColumnName = "start_time"
)
@RenameColumn(
    tableName = "file_operation_logs",
    fromColumnName = "timestamp",
    toColumnName = "start_time"
)
class BasicLogItemStartTimeFinishTime : AutoMigrationSpec