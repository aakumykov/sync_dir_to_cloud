package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage

@Entity(tableName = "comparison_states")
class ComparisonState (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,
    @ColumnInfo(name = "source_object_id") val sourceObjectId: String?,
    @ColumnInfo(name = "target_object_id") val targetObjectId: String?,
    @ColumnInfo(name = "source_object_state") val sourceObjectState: StateInStorage?,
    @ColumnInfo(name = "target_object_state") val targetObjectState: StateInStorage?,
    @ColumnInfo(name = "relative_path") val relativePath: String, // Для удобства отладки, чтобы знать, что за файл.
) {
    @Ignore val isBilateral: Boolean = sourceObjectId != null && targetObjectId != null
    @Ignore val onlySource: Boolean = sourceObjectId != null && targetObjectId == null
    @Ignore val onlyTarget: Boolean = sourceObjectId == null && targetObjectId != null
}

val ComparisonState.isUnchangedNew: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.NEW