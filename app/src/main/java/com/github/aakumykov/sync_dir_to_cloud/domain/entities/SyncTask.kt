package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "sync_tasks")
class SyncTask : SyncTaskBase {

    constructor(syncTaskBase: SyncTaskBase) : super(syncTaskBase.sourcePath, syncTaskBase.targetPath) {
        id = UUID.randomUUID().toString()
        isProgress = false
    }

    constructor(id: String,
                sourcePath: String,
                targetPath: String,
                isProgress: Boolean
    ) : super(sourcePath, targetPath) {
        this.id = id
        this.isProgress = isProgress
    }

    @PrimaryKey val id: String

    @Ignore fun getTitle() = "$sourcePath - $targetPath"

    @ColumnInfo(name = "is_progress") private var isProgress: Boolean

    @ColumnInfo(name = "progress_error") private var progressError: String? = null

    fun setIsProgress(isProgress: Boolean) {
        this.isProgress = isProgress
    }

    fun getIsProgress(): Boolean = isProgress


    fun setProgressError(errorMsg: String?) {
        this.progressError = errorMsg
    }

    fun getProgressError(): String? = progressError

    @Deprecated("Переименовать в updateValuesFromBase()")
    fun updateValues(syncTaskBase: SyncTaskBase): SyncTask {
        return SyncTask(
            id,
            syncTaskBase.sourcePath,
            syncTaskBase.targetPath,
            isProgress
        )
    }
}