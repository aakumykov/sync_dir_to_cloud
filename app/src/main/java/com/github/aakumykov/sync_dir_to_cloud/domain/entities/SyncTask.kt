package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "sync_tasks")
class SyncTask : SyncTaskBase {

    constructor(syncTaskBase: SyncTaskBase) : super(syncTaskBase.sourcePath, syncTaskBase.targetPath) {
        id = UUID.randomUUID().toString()
    }

    constructor(id: String, sourcePath: String, targetPath: String) : super(sourcePath, targetPath) {
        this.id = id
    }

    @PrimaryKey val id: String

    @Ignore
    fun getTitle() = "$sourcePath - $targetPath"

    fun updateValues(syncTaskBase: SyncTaskBase): SyncTask {
        return SyncTask(id, syncTaskBase.sourcePath, syncTaskBase.targetPath)
    }
}