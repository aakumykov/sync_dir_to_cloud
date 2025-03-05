package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
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

    @RenameColumn(tableName = "comparison_states", fromColumnName = "source_object_id", toColumnName = "source_id")
    @RenameColumn(tableName = "comparison_states", fromColumnName = "target_object_id", toColumnName = "target_id")
    @RenameColumn(tableName = "comparison_states", fromColumnName = "source_object_state", toColumnName = "source_state")
    @RenameColumn(tableName = "comparison_states", fromColumnName = "target_object_state", toColumnName = "target_state")
    class RenameStateFieldsSpec : AutoMigrationSpec
}