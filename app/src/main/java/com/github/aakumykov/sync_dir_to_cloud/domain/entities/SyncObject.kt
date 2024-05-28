package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.shiftTwoVersionParameters
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf
import com.github.aakumykov.sync_dir_to_cloud.utils.sha256

// TODO: сложный ключ, включающий taskId, name, parentPath и другое, что составляет уникальность.

@Entity(
    tableName = "sync_objects",
    primaryKeys = [ "storage_half", "id", "task_id", "relative_parent_dir_path", "name" ],
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

    @ColumnInfo(name = "storage_half") val storageHalf: StorageHalf,
    @ColumnInfo(name = "id")           val id: String,
    @ColumnInfo(name = "task_id")      val taskId: String,

    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "relative_parent_dir_path") val relativeParentDirPath: String,
    @ColumnInfo(name = "is_dir") val isDir: Boolean,

    @ColumnInfo(name = "sync_state") val syncState: ExecutionState,
    @ColumnInfo(name = "sync_date") val syncDate: Long,
    @ColumnInfo(name = "sync_error") var syncError: String,

    @ColumnInfo(name = "modification_state") var modificationState: ModificationState,

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
                if (isDir) "[DIR]" else "[FILE]" +
                " name='$name',  storageHalf=$storageHalf, id='$id', taskId='$taskId', relativeParentDirPath='$relativeParentDirPath', isDir=$isDir, syncState=$syncState, syncDate=$syncDate, syncError='$syncError', modificationState=$modificationState, mTime=$mTime, newMTime=$newMTime, size=$size, newSize=$newSize)"
    }


    companion object {

        fun id(fsItem: FSItem): String = sha256(fsItem.absolutePath)

        fun createAsNew(
            storageHalf: StorageHalf,
            taskId: String,
            fsItem: FSItem,
            relativeParentDirPath: String,
        ): SyncObject {

            return SyncObject(
                storageHalf = storageHalf,
                id = id(fsItem),
                taskId = taskId,
                name = fsItem.name,
                relativeParentDirPath = relativeParentDirPath,
                isDir = fsItem.isDir,
                syncState = ExecutionState.NEVER,
                syncError = "",
                modificationState = ModificationState.NEW,
                mTime = fsItem.mTime,
                size = fsItem.size,
                syncDate = 0L,
            )
        }

        fun createAsExisting(existingSyncObject: SyncObject,
                             modifiedFSItem: FSItem,
                             modificationState: ModificationState): SyncObject
        {
            return existingSyncObject.apply {
                existingSyncObject.shiftTwoVersionParameters(modifiedFSItem)
                this.modificationState = modificationState
            }
        }
    }

}