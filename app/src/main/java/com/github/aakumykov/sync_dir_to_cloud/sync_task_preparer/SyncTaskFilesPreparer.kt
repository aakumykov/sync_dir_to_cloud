package com.github.aakumykov.sync_dir_to_cloud.sync_task_preparer

import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.entities.SyncObject
import com.github.aakumykov.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncObjectAdder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

class SyncTaskFilesPreparer @AssistedInject constructor (
    @Assisted private val recursiveDirReader: RecursiveDirReader,
    private val syncObjectAdder: SyncObjectAdder
) {
    suspend fun prepareSyncTask(syncTask: SyncTask) {

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
    }
}