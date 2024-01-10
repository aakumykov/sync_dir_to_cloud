package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncTaskStateDAO {

    @Query("UPDATE sync_tasks SET state = :state WHERE id = :taskId")
    fun setState(taskId: String, state: SyncTask.State)

    @Query("SELECT state FROM sync_tasks WHERE id = :taskId")
    fun getSyncTaskStateFlow(taskId: String): Flow<SyncTask.State>
}