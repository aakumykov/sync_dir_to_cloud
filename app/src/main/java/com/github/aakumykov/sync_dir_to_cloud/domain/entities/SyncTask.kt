package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "sync_tasks")
class SyncTask (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "source_path") var sourcePath: String,
    @ColumnInfo(name = "target_path") var targetPath: String,
    @ColumnInfo(name = "state") var state: State
) {
    constructor(sourcePath: String, targetPath: String) : this(
        UUID.randomUUID().toString(),
        sourcePath,
        targetPath,
        State.DISABLED
    )

    fun getTitle(): String {
        return "$sourcePath -> $targetPath"
    }

    enum class State {
        DISABLED,
        ENABLED,
        RUNNING,
        SUCCESS,
        ERROR,
    }
}