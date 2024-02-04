package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.utils.sha256

@Entity(
    tableName = "sync_objects",
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [
        Index("task_id")
    ]
)
class SyncObject (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    val name: String,
    @ColumnInfo(name = "relative_parent_dir_path") val relativeParentDirPath: String,
    @ColumnInfo(name = "is_dir") val isDir: Boolean,
    val state: State,
    @ColumnInfo(name = "is_progress") val isProgress: Boolean,
    @ColumnInfo(name = "is_success") val isSuccess: Boolean,
    @Deprecated("Переименовтаь в m_time") @ColumnInfo(name = "element_date") val elementDate: Long,
    @ColumnInfo(defaultValue = "0") val size: Long,
    @ColumnInfo(name = "sync_date") val syncDate: Long?,
    @ColumnInfo(name = "error_msg") val errorMsg: String?
) {

    enum class State {
        IDLE,
        RUNNING,
        SUCCESS,
        ERROR
    }

    override fun toString(): String {
        return SyncObject::class.simpleName + " { $relativeParentDirPath/$name ($state) }"
    }

    /*fun toFSItem(): FSItem {
        return when(isDir) {
            true ->
        }
    }*/

    companion object {

        fun id(fsItem: FSItem): String = sha256(fsItem.absolutePath)

        fun create(
            taskId: String,
            fsItem: FSItem,
            relativeParentDirPath: String
        ): SyncObject {

            return SyncObject(
                id = id(fsItem),
                taskId = taskId,
                name = fsItem.name,
                relativeParentDirPath = relativeParentDirPath,
                isDir = fsItem.isDir,
                state = State.IDLE,
                isProgress = false,
                isSuccess = false,
                errorMsg = null,
                elementDate = fsItem.mTime,
                size = fsItem.size,
                syncDate = null,
            )
        }
    }
}