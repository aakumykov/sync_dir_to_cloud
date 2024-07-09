package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.AuthHolder
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

@Entity(
    tableName = "sync_tasks",
    /*foreignKeys = [
        ForeignKey(entity = CloudAuth::class,
            parentColumns = ["id"],
            childColumns = ["source_auth_id"],
            onDelete = NO_ACTION,
            onUpdate = NO_ACTION)
    ],
    indices = [ Index("source_auth_id"), Index("target_auth_id") ]*/
)
class SyncTask {

    // FIXME: var|val

    @PrimaryKey var id: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "notification_id") var notificationId: Int = View.generateViewId()

    @ColumnInfo(name = "state") var state: State = State.IDLE
    @ColumnInfo(name = "is_enabled") var isEnabled: Boolean = false

    @ColumnInfo(name = "scheduling_state") var schedulingState: ExecutionState = ExecutionState.NEVER
    @ColumnInfo(name = "scheduling_error") var schedulingError: String? = null

    @ColumnInfo(name = "execution_state") var executionState: ExecutionState = ExecutionState.NEVER
    @ColumnInfo(name = "execution_error") var executionError: String? = null

    @ColumnInfo(name = "source_reading_state") var sourceReadingState: ExecutionState
    @ColumnInfo(name = "target_reading_state") var targetReadingState: ExecutionState

    @ColumnInfo(name = "source_reading_error") var sourceReadingError: String? = null
    @ColumnInfo(name = "target_reading_error") var targetReadingError: String? = null

    @Deprecated("Не может быть null, реши этот вопрос")
    @ColumnInfo(name = "source_storage_type") var sourceStorageType: StorageType?

    @Deprecated("Не может быть null, реши этот вопрос")
    @ColumnInfo(name = "target_storage_type") var targetStorageType: StorageType?

    @Deprecated("Не может быть null, реши этот вопрос")
    @ColumnInfo(name = "source_path") var sourcePath: String? // FIXME: не-null

    @Deprecated("Не может быть null, реши этот вопрос")
    @ColumnInfo(name = "target_path") var targetPath: String? // FIXME: не-null

    @ColumnInfo(name = "sync_mode") var syncMode: SyncMode?

    @ColumnInfo(name = "interval_h") var intervalHours: Int
    @ColumnInfo(name = "interval_m") var intervalMinutes: Int

    @ColumnInfo(name = "old_interval_h") var oldIntervalH: Int
    @ColumnInfo(name = "old_interval_m") var oldIntervalM: Int

    @ColumnInfo(name = "source_auth_id") var sourceAuthId: String? = null
    @ColumnInfo(name = "target_auth_id") var targetAuthId: String? = null

    @ColumnInfo(name = "c_time") var cTime: Long = Date().time
    @ColumnInfo(name = "last_start") var lastStart: Long? = null
    @ColumnInfo(name = "last_finish") var lastFinish: Long? = null

    @ColumnInfo(name = "total_objects_count", defaultValue = "0") var totalObjectsCount: Int = 0
    @ColumnInfo(name = "synced_objects_count", defaultValue = "0") var syncedObjectsCount: Int = 0


    @Ignore
    constructor() {
        this.sourceStorageType = null
        this.targetStorageType = null

        this.sourcePath = null
        this.targetPath = null

        this.syncMode = null

        this.intervalHours = 0
        this.intervalMinutes = 0

        this.oldIntervalH = 0
        this.oldIntervalM = 0

        this.sourceReadingState = ExecutionState.NEVER
        this.targetReadingState = ExecutionState.NEVER
    }

    constructor(sourcePath: String,
                targetStorageType: StorageType,
                sourceStorageType: StorageType,
                targetPath: String,
                syncMode: SyncMode?,
                intervalHours: Int,
                intervalMinutes: Int
    ) : this() {
        this.sourceStorageType = sourceStorageType
        this.targetStorageType = targetStorageType

        this.sourcePath = sourcePath
        this.targetPath = targetPath

        this.syncMode = syncMode

        this.intervalHours = intervalHours
        this.intervalMinutes = intervalMinutes

        this.oldIntervalH = 0
        this.oldIntervalM = 0

        this.sourceReadingState = ExecutionState.NEVER
        this.targetReadingState = ExecutionState.NEVER
    }


    fun getTitle(): String {
        return "$sourcePath -> ${targetStorageType}:${targetPath}"
    }


    @Ignore
    fun getExecutionInterval(): Pair<Long, TimeUnit> {
        return Pair(intervalHours * 60L + intervalMinutes, TimeUnit.MINUTES)
    }


    override fun toString(): String {
        return SyncTask::class.simpleName +
                " { " +
                (if(isEnabled) "[enabled]" else "[disabled]") +
                ": $sourceStorageType($sourcePath) -> $targetStorageType($targetPath) " +
                "}"
    }

    @Ignore
    fun summary() = "SyncTask ($id): $sourcePath --> $targetPath"

    @Ignore
    val description: String = summary()

    enum class State {
        IDLE,
        READING_SOURCE,
        WRITING_TARGET,
        @Deprecated("Нужно использовать 'запланировано'") SUCCESS,
        EXECUTION_ERROR,
        SCHEDULING_ERROR,
        SEMI_SUCCESS;
    }

    companion object {
        val TAG = SyncTask::class.simpleName.toString()
    }
}