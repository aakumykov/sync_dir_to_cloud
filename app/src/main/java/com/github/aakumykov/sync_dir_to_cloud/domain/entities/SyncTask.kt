package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class SyncTask : SyncTaskBase {

    @PrimaryKey
    val id: String

    constructor(syncTaskBase: SyncTaskBase) : super(syncTaskBase.sourcePath, syncTaskBase.targetPath) {
        id = UUID.randomUUID().toString()
    }

    constructor(id: String, sourcePath: String, targetPath: String) : super(sourcePath, targetPath) {
        this.id = id
    }
}