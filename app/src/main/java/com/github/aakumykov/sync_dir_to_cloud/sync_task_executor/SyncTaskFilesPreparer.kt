package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationShower
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

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

        var currentSyncObject: SyncObject? = null

//        try {
            recursiveDirReader.getRecursiveList(syncTask.sourcePath!!).forEach { fileListItem ->
                val parentId = if (null == currentSyncObject) "" else currentSyncObject?.id
                currentSyncObject = SyncObject.create(syncTask.id, parentId!!, fileListItem)
                syncObjectAdder.addSyncObject(currentSyncObject!!)
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