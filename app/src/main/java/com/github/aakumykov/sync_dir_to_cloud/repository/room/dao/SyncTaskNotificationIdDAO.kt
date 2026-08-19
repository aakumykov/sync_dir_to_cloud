package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncTaskNotificationIdDAO {

    @Query("UPDATE sync_tasks SET current_notification_id = :notificationId WHERE id = :taskId")
    fun setNotificationId(taskId: String, notificationId: Int)

    @Query("SELECT current_notification_id FROM sync_tasks WHERE id = :taskId")
    fun getNotificationId(taskId: String): Int?
}