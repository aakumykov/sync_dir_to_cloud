package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sync_tasks",
    foreignKeys = [
        ForeignKey(entity = CloudAuth::class,
            parentColumns = ["id"],
            childColumns = ["cloud_auth_id"],
            onDelete = NO_ACTION,
            onUpdate = NO_ACTION)
    ],
    indices = [ Index("cloud_auth_id") ]
)
class SyncTask {

    @PrimaryKey var id: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "state") var state: State = State.IDLE
    @ColumnInfo(name = "is_enabled") var isEnabled: Boolean = false

    @ColumnInfo(name = "source_path") var sourcePath: String? // FIXME: не-null
    @ColumnInfo(name = "target_path") var targetPath: String? // FIXME: не-null
    @ColumnInfo(name = "interval_h") var intervalHours: Int
    @ColumnInfo(name = "interval_m") var intervalMinutes: Int

    @ColumnInfo(name = "cloud_auth_id") var cloudAuthId: String? = null

    @Ignore
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


    override fun toString(): String {
        return SyncTask::class.simpleName + " { enabled: " + isEnabled + ", " + sourcePath + " -> " + targetPath + " }"
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