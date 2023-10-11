package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "sync_tasks")
class SyncTask {

    @PrimaryKey var id: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "state") var state: State = State.IDLE
    @ColumnInfo(name = "is_enabled") var isEnabled: Boolean = false

    @ColumnInfo(name = "source_path") var sourcePath: String?
    @ColumnInfo(name = "target_path") var targetPath: String?
    @ColumnInfo(name = "interval_h") var intervalHours: Int
    @ColumnInfo(name = "interval_m") var intervalMinutes: Int


    constructor() {
        this.sourcePath = null
        this.targetPath = null
        this.intervalHours = 0
        this.intervalMinutes = 0
    }

    constructor(sourcePath: String, targetPath: String, intervalHours: Int, intervalMinutes: Int) : this() {
        this.sourcePath = sourcePath
        this.targetPath = targetPath
        this.intervalHours = intervalHours
        this.intervalMinutes = intervalMinutes
    }


    fun getTitle(): String {
        return "$sourcePath -> $targetPath"
    }


    @Ignore
    fun getExecutionIntervalMinutes(): Long {
        return (intervalHours * 60 + intervalMinutes) * 60L
    }


    enum class State {
        IDLE,
        RUNNING,
        SUCCESS,
        ERROR,
    }

    companion object {
        val TAG = SyncTask::class.simpleName.toString()
    }
}