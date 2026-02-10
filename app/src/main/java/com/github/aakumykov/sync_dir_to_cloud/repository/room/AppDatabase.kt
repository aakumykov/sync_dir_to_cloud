package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.FileOperationLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.InstructionLoggingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateSetterDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskBackupDirDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TaskLogger2DAO

@Database(
    entities = [
        SyncTask::class,
        SyncObject::class,
        CloudAuth::class,
        TaskLogEntry::class,
        ComparisonState::class,
        SyncInstruction::class,
        TaskLogItem::class,
        InstructionLogItem::class,
        FileOperationLogItem::class,
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
        AutoMigration(from = 112, to = 113), // Новый объект FileOperationLogItem
        AutoMigration(from = 113, to = 114), // Новое поле FileOperationLogItem.errorMsg
        AutoMigration(from = 114, to = 115), // Внешний ключ в FileOperationLogItem
        AutoMigration(from = 115, to = 116), // Внешний ключ в ExecutionLogItem
        AutoMigration(from = 116, to = 117), // Внешний ключ в TaskLogEntry
        AutoMigration(from = 117, to = 118, spec = DeleteTableSyncInstructions::class), // Удалил SyncInstruction
        AutoMigration(from = 118, to = 119, spec = RenameTableFromSyncInstructions6ToSyncInstructions::class), // Удалил SyncInstruction
        AutoMigration(from = 119, to = 120), // Новые поля SyncTask.sourceBackupDir, targetBackupDir
        AutoMigration(from = 120, to = 121), // Индексы поля task_id в ComparisonState, SyncInstruction, ExecutionLogItem, FileOperationLogItem, TaskLogEntry.
        AutoMigration(from = 121, to = 122), // Новые поля SyncTask.sourceExecutionBackupDir, targetExecutionBackupDir
        AutoMigration(from = 122, to = 123), // Новое поле SyncInstruction.partsLabel
        AutoMigration(from = 123, to = 124, spec = RenameSyncTaskBackupDirToDirName::class),
        AutoMigration(from = 124, to = 125, spec = RenameSyncTaskBackupDirNameToTaskBackupDirName::class),
        AutoMigration(from = 125, to = 126), // Новое поле ExecutionLogItem.details: String?
        AutoMigration(from = 126, to = 127), // Новое поле FileOperationLogItem.jobId: String?
        AutoMigration(from = 127, to = 128, spec = RenameTableFromExecutionLogToTaskExecutionLog::class), // Переименование таблицы "execution_log" в "task_execution_log".
        AutoMigration(from = 128, to = 129, spec = RenameTableFromSyncOperationLogItemToFileOperationLogItem::class), // Переименование таблицы "sync_operation_logs" в "file_operation_logs".
        AutoMigration(from = 129, to = 130), // Новый объект [TaskLogItem]
        AutoMigration(from = 130, to = 131), // fix: внешний ключ в TaskLogItem
        AutoMigration(from = 131, to = 132), // Новый объект [InstructionLogItem]
        AutoMigration(from = 132, to = 133), // Новый объект [FileOperationLogItem]
        AutoMigration(from = 133, to = 134, spec = RenameLogEntryTypeToLogItemType::class),
        AutoMigration(from = 134, to = 135, spec = DeleteFilePathFromFileOperationLogItem2::class), // FileOperationLogItem: +description -filePath
        AutoMigration(from = 135, to = 136), // FileOperationLogItem().description стало nullable.
        AutoMigration(from = 136, to = 137), // FileOperationLogItem().description вновь не-nullable.
        AutoMigration(from = 137, to = 138, spec = FileOperationLogItem2SourceItemTargetItem::class), // FileOperationLogItem().sourceItem,targetItem.
        AutoMigration(from = 138, to = 139), // FileOperationLogItem().firstItem теперь nullable.
        AutoMigration(from = 139, to = 140, spec = FileOperationLogItem2RenameFirstSecondItems::class),
        AutoMigration(from = 140, to = 141, spec = FileOperationLogItemDeletion::class),
        AutoMigration(from = 141, to = 142, spec = SyncObjectLogItemDeletion::class),
        AutoMigration(from = 142, to = 143, spec = TaskExecutionLogItemDeletion::class),
        AutoMigration(from = 143, to = 144, spec = RenameFileOperationLogItem2ToFileOperationLogItem::class),
        AutoMigration(from = 144, to = 145), // Новые поля "log_item_about".
    ],
    version = 145,
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
    abstract fun getComparisonStateDAO(): ComparisonStateDAO
    abstract fun getSyncInstructionDAO6(): SyncInstructionDAO
    abstract fun getSyncTaskBackupDirDAO(): SyncTaskBackupDirDAO
    abstract fun getTaskLogger2DAO(): TaskLogger2DAO
    abstract fun getInstructionLoggingDAO(): InstructionLoggingDAO
    abstract fun getFileOperationLogDAO(): FileOperationLogDAO
}