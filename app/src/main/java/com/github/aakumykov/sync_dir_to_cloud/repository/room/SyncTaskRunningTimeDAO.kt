package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncTaskRunningTimeDAO {
    
    @Query("UPDATE sync_tasks SET last_start = :time WHERE id = :taskId")
    suspend fun updateStartTime(taskId: String, time: Long)

    @Query("UPDATE sync_tasks SET last_finish = :time WHERE id = :taskId")
    suspend fun updateFinishTime(taskId: String, time: Long)

    @Query("UPDATE sync_tasks SET last_finish = 0 WHERE id = :taskId")
    fun clearFinishTime(taskId: String)
}