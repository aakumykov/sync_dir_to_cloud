package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.shiftTwoVersionParameters
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.utils.sha256

// TODO: сложный ключ, включающий taskId, name, parentPath и другое, что составляет уникальность.

@Entity(
    tableName = "sync_objects",
    primaryKeys = [
        "id",
        "task_id",
        "execution_id",
        "relative_parent_dir_path",
        "name",
        "sync_side"
    ],
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    // TODO: дополнить, оптимизировать индекс
    indices = [
        Index("task_id")
    ]
)
class SyncObject (
    // FIXME: id не должно быть var !!!
    @ColumnInfo(name = "id")           var id: String,

    @ColumnInfo(name = "task_id")      val taskId: String,

    @ColumnInfo(name = "execution_id", defaultValue = "none") var executionId: String,

    @ColumnInfo(name = "sync_side", defaultValue = "SOURCE") var syncSide: SyncSide,

    @ColumnInfo(name = "name") val name: String,

    @ColumnInfo(name = "relative_parent_dir_path") val relativeParentDirPath: String,

    @ColumnInfo(name = "is_dir") val isDir: Boolean,

    @ColumnInfo(name = "is_exists_in_target") val isExistsInTarget: Boolean,

    @ColumnInfo(name = "target_reading_state") var targetReadingState: ExecutionState,
    @ColumnInfo(name = "backup_state") val backupState: ExecutionState,
    @ColumnInfo(name = "deletion_state") val deletionState: ExecutionState,
    @ColumnInfo(name = "restoration_state") val restorationState: ExecutionState,

    @ColumnInfo(name = "sync_state") val syncState: ExecutionState,

    @Deprecated("Переименовать в operationDate")
    @ColumnInfo(name = "sync_date") val syncDate: Long,

    @Deprecated("Переименовать в operationError")
    @ColumnInfo(name = "sync_error") var syncError: String,

    @ColumnInfo(name = "state_in_storage", defaultValue = "UNCHANGED") var stateInStorage: StateInStorage,

    @ColumnInfo(name = "m_time") var mTime: Long,
    @ColumnInfo(name = "new_m_time") var newMTime: Long? = null,

    @ColumnInfo(name = "size") var size: Long,
    @ColumnInfo(name = "new_size") var newSize: Long? = null,
) {
    fun toFSItem(basePath: String): FSItem {
        return SimpleFSItem(
            name = name,
            parentPath = relativeParentDirPath,
            absolutePath = basePath + CloudWriter.DS + relativeParentDirPath + CloudWriter.DS + name,
            isDir = isDir,
            mTime = mTime,
            size = size
        )
    }

    override fun toString(): String {
        return "SyncObject( " +
                (if (isDir) "[DIR]" else "[FILE]") +
                " name='$name', id='$id', taskId='$taskId', relativeParentDirPath='$relativeParentDirPath', isDir=$isDir, syncState=$syncState, syncDate=$syncDate, syncError='$syncError', modificationState=$stateInStorage, mTime=$mTime, newMTime=$newMTime, size=$size, newSize=$newSize)"
    }


    companion object {

        fun id(fsItem: FSItem): String = sha256(fsItem.absolutePath)

        fun createAsNew(
            taskId: String,
            executionId: String,
            syncSide: SyncSide,
            fsItem: FSItem,
            relativeParentDirPath: String,
        ): SyncObject {

            return SyncObject(
                id = randomUUID,
                taskId = taskId,
                executionId = executionId,
                syncSide = syncSide,
                name = fsItem.name,
                relativeParentDirPath = relativeParentDirPath,
                isDir = fsItem.isDir,
                isExistsInTarget = false,
                targetReadingState = ExecutionState.NEVER,
                backupState = ExecutionState.NEVER,
                deletionState = ExecutionState.NEVER,
                restorationState = ExecutionState.NEVER,
                syncState = ExecutionState.NEVER,
                syncDate = 0L,
                syncError = "",
                stateInStorage = StateInStorage.NEW,
                mTime = fsItem.mTime,
                size = fsItem.size,
            )
        }


        fun createFromExisting(
            syncObject: SyncObject,
            modifiedFSItem: FSItem,
            newExecutionId: String
        ): SyncObject {
            return syncObject.apply {
                id = randomUUID
                syncObject.shiftTwoVersionParameters(modifiedFSItem)
                executionId = newExecutionId
            }
        }


        @Deprecated("Используется устаревшим кодом")
        fun createFromExisting(syncObject: SyncObject,
                               newExecutionId: String,
                               newSyncSide: SyncSide,
                               newStateInStorage: StateInStorage): SyncObject {
            return syncObject.apply {
                id = randomUUID
                executionId = newExecutionId
                syncSide = newSyncSide
                stateInStorage = newStateInStorage
            }
        }


        fun createFromExisting(syncObject: SyncObject,
                               newExecutionId: String,
                               newSyncSide: SyncSide): SyncObject {
            return syncObject.apply {
                id = randomUUID
                executionId = newExecutionId
                syncSide = newSyncSide
            }
        }
    }

    /*override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            !is SyncObject -> false
            else -> {
                return (mTime != other.mTime) ||
                        (size != other.size) ||
                        (stateInSource != other.stateInSource)
            }
        }
    }*/

    @RenameColumn(tableName = "sync_objects", fromColumnName = "side", toColumnName = "sync_side")
    class RenameSideToSyncSideMigration : AutoMigrationSpec


    @RenameColumn(tableName = "sync_objects", fromColumnName = "state_in_source", toColumnName = "state_in_source")
    class RenameStateInSourceToStateInStorageMigration : AutoMigrationSpec
}