package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Entity(
    tableName = "comparison_states",
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
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
    @Ignore val isBilateral: Boolean = sourceObjectId != null && targetObjectId != null
    @Ignore val onlySource: Boolean = sourceObjectId != null && targetObjectId == null
    @Ignore val onlyTarget: Boolean = sourceObjectId == null && targetObjectId != null

    /*override fun toString(): String {
        return "ComparisonState(id='$id', taskId='$taskId', executionId='$executionId', sourceObjectId=$sourceObjectId, targetObjectId=$targetObjectId, sourceObjectState=$sourceObjectState, targetObjectState=$targetObjectState, relativePath='$relativePath', isBilateral=$isBilateral, onlySource=$onlySource, onlyTarget=$onlyTarget)"
    }*/

    @Ignore
    override fun toString(): String {
        return "ComparisonState('$relativePath', s:$sourceObjectState, t:$targetObjectState)"
    }
}


val ComparisonState.isFile: Boolean get() = !isDir

val ComparisonState.notDeletedInTarget: Boolean
    get() = targetObjectState != StateInStorage.DELETED

val ComparisonState.isDeletedInSource: Boolean
    get() = sourceObjectState == StateInStorage.DELETED

val ComparisonState.notMutuallyUnchanged: Boolean
    get() = !(sourceObjectState == StateInStorage.UNCHANGED &&
            targetObjectState == StateInStorage.UNCHANGED)

val ComparisonState.notUnchangedOrDeletedInTarget: Boolean
    get() = targetObjectState != StateInStorage.UNCHANGED &&
            targetObjectState != StateInStorage.DELETED

val ComparisonState.notUnchangedOrDeletedInSource: Boolean
    get() = sourceObjectState != StateInStorage.UNCHANGED &&
            sourceObjectState != StateInStorage.DELETED


val ComparisonState.isSourceUnchangedTargetNew: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isSourceUnchangedTargetModified: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isSourceUnchangedTargetDeleted: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.DELETED


val ComparisonState.isSourceNewAndTargetUnchanged: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.UNCHANGED

val ComparisonState.isNewAndNew: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isNewAndModified: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isNewAndDeleted: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.DELETED


val ComparisonState.isModifiedAndUnchanged: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.UNCHANGED

val ComparisonState.isModifiedAndNew: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isModifiedAndModified: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isModifiedAndDeleted: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.DELETED



val ComparisonState.isDeletedAndUnchanged: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.UNCHANGED

val ComparisonState.isDeletedAndNew: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isDeletedAndModified: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isDeletedAndDeleted: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.DELETED

