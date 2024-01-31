package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
interface SyncTaskStateDAO {

    @Deprecated("Используй suspend-вариант")
    @Query("UPDATE sync_tasks SET state = :state WHERE id = :taskId")
    fun setState(taskId: String, state: SyncTask.State)

    @Query("UPDATE sync_tasks SET state = :state WHERE id = :taskId")
    suspend fun setStateSuspend(taskId: String, state: SyncTask.State)

    @Query("UPDATE sync_tasks SET is_enabled = :isEnabled WHERE id = :taskId")
    suspend fun setEnabled(taskId: String, isEnabled: Boolean)
}