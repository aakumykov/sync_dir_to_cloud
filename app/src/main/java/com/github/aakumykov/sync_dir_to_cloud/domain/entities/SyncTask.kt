package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

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

    // FIXME: var|val

    @PrimaryKey var id: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "notification_id") var notificationId: Int = View.generateViewId()

    @ColumnInfo(name = "state") var state: State = State.IDLE
    @ColumnInfo(name = "is_enabled") var isEnabled: Boolean = false

    @ColumnInfo(name = "scheduling_state") var schedulingState: ExecutionState = ExecutionState.IDLE
    @ColumnInfo(name = "scheduling_error") var schedulingError: String? = null

    @ColumnInfo(name = "execution_state") var executionState: ExecutionState = ExecutionState.IDLE
    @ColumnInfo(name = "execution_error") var executionError: String? = null

    @ColumnInfo(name = "source_type") var sourceType: StorageType?
    @ColumnInfo(name = "target_type") var targetType: StorageType?

    @ColumnInfo(name = "source_path") var sourcePath: String? // FIXME: не-null
    @ColumnInfo(name = "target_path") var targetPath: String? // FIXME: не-null

    @ColumnInfo(name = "interval_h") var intervalHours: Int
    @ColumnInfo(name = "interval_m") var intervalMinutes: Int

    @ColumnInfo(name = "cloud_auth_id") var cloudAuthId: String? = null  // FIXME: не-null

    @ColumnInfo(name = "c_time") var cTime: Long = Date().time

    @ColumnInfo(name = "total_objects_count", defaultValue = "0") var totalObjectsCount: Int = 0
    @ColumnInfo(name = "synced_objects_count", defaultValue = "0") var syncedObjectsCount: Int = 0


    @Ignore
    constructor() {
        this.sourceType = StorageType.LOCAL
        this.targetType = null

        this.sourcePath = null
        this.targetPath = null

        this.intervalHours = 0
        this.intervalMinutes = 0
    }

    constructor(sourcePath: String,
                targetType: StorageType,
                sourceType: StorageType,
                targetPath: String,
                intervalHours: Int,
                intervalMinutes: Int
    ) : this() {
        this.sourcePath = sourcePath
        this.targetType = targetType
        this.sourceType = sourceType
        this.targetPath = targetPath
        this.intervalHours = intervalHours
        this.intervalMinutes = intervalMinutes
    }


    fun getTitle(): String {
        return "$sourcePath -> ${targetType}:${targetPath}"
    }


    @Ignore
    fun getExecutionInterval(): Pair<Long, TimeUnit> {
        return Pair(intervalHours * 60L + intervalMinutes, TimeUnit.MINUTES)
    }


    override fun toString(): String {
        return SyncTask::class.simpleName +
                " { enabled: $isEnabled, $sourcePath -> $targetType:$targetPath }"
    }

    @Ignore
    fun summary() = "SyncTask: $sourcePath --> $targetPath"

    enum class State {
        IDLE,
        READING_SOURCE,
        WRITING_TARGET,
        @Deprecated("Нужно использовать 'запланировано'") SUCCESS,
        EXECUTION_ERROR,
        SCHEDULING_ERROR,
        SEMI_SUCCESS
    }

    companion object {
        val TAG = SyncTask::class.simpleName.toString()
    }
}