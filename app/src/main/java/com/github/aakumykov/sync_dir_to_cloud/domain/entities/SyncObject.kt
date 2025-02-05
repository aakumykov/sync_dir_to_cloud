package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject.Companion.SIDE_KEY
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.shiftTwoVersionParameters
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.Side
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
        SIDE_KEY
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
    @ColumnInfo(name = "id")           val id: String,

    @ColumnInfo(name = "task_id")      val taskId: String,

    @ColumnInfo(name = "execution_id", defaultValue = "none") var executionId: String,

    @ColumnInfo(name = SIDE_KEY, defaultValue = SIDE_KEY_DEFAULT) val side: Side,

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

    @ColumnInfo(name = "state_in_source") var stateInStorage: StateInStorage,

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

        const val SIDE_KEY = "side"
        const val SIDE_KEY_DEFAULT = "SOURCE"

        fun id(fsItem: FSItem): String = sha256(fsItem.absolutePath)

        fun createAsNew(
            taskId: String,
            executionId: String,
            side: Side,
            fsItem: FSItem,
            relativeParentDirPath: String,
        ): SyncObject {

            return SyncObject(
                id = id(fsItem),
                taskId = taskId,
                executionId = executionId,
                side = side,
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

        @Deprecated("разберись с аргументом stateInStorage")
        fun createFromExisting(executionId: String,
                               existingSyncObject: SyncObject,
                               modifiedFSItem: FSItem,
                               stateInStorage: StateInStorage): SyncObject
        {
            return existingSyncObject.apply {
                existingSyncObject.shiftTwoVersionParameters(
                    modifiedFSItem
                )
                this.executionId = executionId
//                this.stateInStorage = stateInStorage
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
}