package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.enums.StateInStorage

@Entity(
    tableName = "comparison_states",
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id"])
    ]
)
class ComparisonState (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,

    @ColumnInfo(name = "is_dir", defaultValue = "false") val isDir: Boolean,

    @ColumnInfo(name = "source_object_id") val sourceObjectId: String?,
    @ColumnInfo(name = "target_object_id") val targetObjectId: String?,

    @ColumnInfo(name = "source_object_state") val sourceObjectState: StateInStorage?,
    @ColumnInfo(name = "target_object_state") val targetObjectState: StateInStorage?,

    @ColumnInfo(name = "relative_path") val relativePath: String, // Для удобства отладки, чтобы знать, что за файл.
) {
    @Ignore
    val isBilateral: Boolean = sourceObjectId != null && targetObjectId != null
    @Ignore
    val onlySource: Boolean = sourceObjectId != null && targetObjectId == null
    @Ignore
    val onlyTarget: Boolean = sourceObjectId == null && targetObjectId != null

    /*override fun toString(): String {
        return "ComparisonState(id='$id', taskId='$taskId', executionId='$executionId', sourceObjectId=$sourceObjectId, targetObjectId=$targetObjectId, sourceObjectState=$sourceObjectState, targetObjectState=$targetObjectState, relativePath='$relativePath', isBilateral=$isBilateral, onlySource=$onlySource, onlyTarget=$onlyTarget)"
    }*/

    @Ignore
    override fun toString(): String {
        return "ComparisonState(relativePath: '$relativePath', s:$sourceObjectState, t:$targetObjectState)"
    }
}