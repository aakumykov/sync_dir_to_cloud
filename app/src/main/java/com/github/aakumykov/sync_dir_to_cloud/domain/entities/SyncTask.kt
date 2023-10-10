package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.config.SyncTaskExecutionConfig
import java.util.*

@Entity(tableName = "sync_tasks")
class SyncTask(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "source_path") var sourcePath: String,
    @ColumnInfo(name = "target_path") var targetPath: String,
    @ColumnInfo(name = "state") var state: State, // TODO: OpState
    @ColumnInfo(name = "enabled") var enabled: Boolean,
    @ColumnInfo(name = "interval_h") var intervalHours: Int,
    @ColumnInfo(name = "interval_m") var intervalMinutes: Int
) {
    constructor(sourcePath: String, targetPath: String) : this(
        UUID.randomUUID().toString(),
        sourcePath,
        targetPath,
        State.IDLE,
        false,
        SyncTaskExecutionConfig.DEFAULT_EXECUTION_PERIOD_HOURS,
        SyncTaskExecutionConfig.DEFAULT_EXECUTION_PERIOD_MINUTES
    )

    fun getTitle(): String {
        return "$sourcePath -> $targetPath"
    }


    @Ignore
    fun getExecutionIntervalMinutes(): Long {
        return (intervalHours * 60 + intervalMinutes) * 60 * 1000L
    }

    // TODO: поменять на OpState
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