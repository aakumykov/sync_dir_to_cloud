package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TargetObject.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
class TargetObject(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "is_dir")
    val isDir: Boolean,

    val name: String,

    @ColumnInfo(name = "relative_parent_dir_path")
    val relativeParentDirPath: String,
) {
    constructor(syncObject: SyncObject) : this(
        id = syncObject.id,
        taskId = syncObject.taskId,
        isDir = syncObject.isDir,
        name = syncObject.name,
        relativeParentDirPath = syncObject.relativeParentDirPath,
    )

    companion object {

        fun from(taskId: String, fileListItem: RecursiveDirReader.FileListItem): TargetObject {
            return TargetObject(SyncObject.createAsNew(
                taskId = taskId,
                fsItem = fileListItem,
                relativeParentDirPath = ,
            ))
        }

        const val TABLE_NAME = "target_objects"
    }
}