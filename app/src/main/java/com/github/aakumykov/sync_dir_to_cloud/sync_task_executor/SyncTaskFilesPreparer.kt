package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationShower
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.UUID

class SyncTaskFilesPreparer @AssistedInject constructor (
    @Assisted private val recursiveDirReader: RecursiveDirReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val notificationShower: SyncTaskNotificationShower,
) {
    suspend fun prepareSyncTask(syncTask: SyncTask) {
        Log.d(TAG, "prepareSyncTask(${syncTask.id})")

        syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.READING_SOURCE)

        notificationShower.showNotification(syncTask.id)

        delay(3000)

//        try {
            recursiveDirReader.getRecursiveList(syncTask.sourcePath!!).forEach { fileListItem ->
                val syncObject = SyncObject(
                    id = UUID.randomUUID().toString(),
                    taskId = syncTask.id,
                    name = fileListItem.name,
                    path = fileListItem.absolutePath,
                    state = SyncObject.State.IDLE,
                    isDir = fileListItem.isDir,
                    isProgress = false,
                    isSuccess = false,
                    errorMsg = null,
                    elementDate = fileListItem.cTime,
                    syncDate = null,
                )

                syncObjectAdder.addSyncObject(syncObject)
            }
        /*}
        catch (t: Throwable) {
            syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.ERROR)
        }*/
    }

    @AssistedFactory
    interface Factory {
        fun create(recursiveDirReader: RecursiveDirReader): SyncTaskFilesPreparer
    }

    companion object {
        val TAG: String = SyncTaskFilesPreparer::class.java.simpleName
    }
}