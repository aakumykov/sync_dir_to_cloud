package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.github.aakumykov.fs_item.FSItem
import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
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
    val path: String,
    val state: State,
    @ColumnInfo(name = "is_dir") val isDir: Boolean,
    @ColumnInfo(name = "is_progress") val isProgress: Boolean,
    @ColumnInfo(name = "is_success") val isSuccess: Boolean,
    @ColumnInfo(name = "element_date") val elementDate: Long,
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
        return SyncObject::class.simpleName + " { ($state) $name, $path }"
    }

    companion object {

        fun id(fsItem: FSItem): String = sha256(fsItem.absolutePath)

        fun create(taskId: String, fsItem: FSItem): SyncObject {
            return SyncObject(
                id = id(fsItem),
                taskId = taskId,
                name = fsItem.name,
                path = fsItem.absolutePath,
                state = State.IDLE,
                isDir = fsItem.isDir,
                isProgress = false,
                isSuccess = false,
                errorMsg = null,
                elementDate = fsItem.cTime,
                syncDate = null,
            )
        }
    }
}