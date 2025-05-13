package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncTaskBackupDirDAO {

    @Query("UPDATE sync_tasks SET source_backup_dir = :dirName WHERE id = :taskId")
    fun setSourceBackupDir(taskId: String, dirName: String)

    @Query("UPDATE sync_tasks SET target_backup_dir = :dirName WHERE id = :taskId")
    fun setTargetBackupDir(taskId: String, dirName: String)

    @Query("UPDATE sync_tasks SET source_execution_backup_dir = :dirName WHERE id = :taskId")
    fun setSourceExecutionBackupDir(taskId: String, dirName: String)

    @Query("UPDATE sync_tasks SET target_execution_backup_dir = :dirName WHERE id = :taskId")
    fun setTargetExecutionBackupDir(taskId: String, dirName: String)
}
