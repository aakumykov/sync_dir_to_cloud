package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationShower
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

class SyncTargetWriter @AssistedInject constructor(
    @Assisted private val targetStorageType: StorageType,
    @Assisted private val targetAuthToken: String,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val notificationShower: SyncTaskNotificationShower
){
    suspend fun writeToTarget(syncTask: SyncTask) {
        Log.d(TAG, "writeToTarget(${syncTask.id})")

        syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.WRITING_TARGET)

        notificationShower.showNotification(syncTask.id)

        delay(3000)
    }


    @AssistedFactory
    interface Factory {
        fun create(targetStorageType: StorageType, targetAuthToken: String): SyncTargetWriter
    }

    companion object {
        val TAG: String = SyncTargetWriter::class.java.simpleName
    }
}
