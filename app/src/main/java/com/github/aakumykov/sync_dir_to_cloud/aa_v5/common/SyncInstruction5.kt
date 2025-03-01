package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide

@Entity(
    tableName = "sync_instructions_5",
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class SyncInstruction5 (
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "task_id", index = true) val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,
    @ColumnInfo(name = "execution_order_num", defaultValue = "0") val executionOrderNum: Int,
    @ColumnInfo(name = "group_order_num", defaultValue = "0") val groupOrderNum: Int,

    @ColumnInfo(name = "source_object_id") val sourceObjectId: String?,
    @ColumnInfo(name = "target_object_id", defaultValue = "null") val targetObjectId: String?,


    @ColumnInfo(name = "sync_side") val syncSide: SyncSide,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_dir") val isDir: Boolean,
    @ColumnInfo(name = "sync_operation") val operation: SyncOperation,
) {
    @RenameColumn(tableName = "sync_instructions_5", fromColumnName = "order_num", toColumnName = "execution_order_num")
    class RenameOrderNumToGroupOrderNumMigrationSpec : AutoMigrationSpec

    @RenameColumn(tableName = "sync_instructions_5", fromColumnName = "object_id", toColumnName = "source_object_id")
    class RenameObjectIdToSourceObjectIdMigrationSpec : AutoMigrationSpec
}
