package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
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
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO


@Database(
    entities = [
        SyncTask::class,
        SyncObject::class,
        CloudAuth::class,
        TaskLogEntry::class,
        SyncObjectLogItem::class
   ],
    version = 75,
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
    ]
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
}