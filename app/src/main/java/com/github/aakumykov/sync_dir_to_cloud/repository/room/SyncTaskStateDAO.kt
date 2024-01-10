package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncTaskStateDAO {

    @Query("UPDATE sync_tasks SET state = :state WHERE id = :taskId")
    fun setState(taskId: String, state: SyncTask.State)

    @Query("SELECT state FROM sync_tasks WHERE id = :taskId")
    fun getState(taskId: String): LiveData<SyncTask.State>

    @Query("SELECT state FROM sync_tasks WHERE id = :taskId")
    suspend fun getStateAsFlow(@TypeConverters(SyncTaskStateConverter::class) taskId: String): Flow<SyncTask.State>
}

class SyncTaskStateConverter {
    @TypeConverter
    fun taskStateToString(state: SyncTask.State) = state.name

    @TypeConverter
    fun stringToTaskState(stateName: String) = SyncTask.State.valueOf(stateName)
}