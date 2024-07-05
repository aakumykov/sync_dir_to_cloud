package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.config.CloudType.Companion.list
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

/**
 * Создаёт каталоги, относящиеся к SyncTask-у.
 */
class SyncTaskDirsCreator @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDirCreatorCreator: SyncObjectDirCreatorCreator
){
    suspend fun createNewDirsFromTask(syncTask: SyncTask) {
        syncObjectReader.getObjectsForTaskWithModificationState(syncTask.id, ModificationState.NEW)
            .filter { it.isDir }
            .also { list -> createDirs(list, syncTask) }
    }

    private suspend fun createDirs(dirList: List<SyncObject>, syncTask: SyncTask) {

        syncObjectDirCreatorCreator.createFor(syncTask)?.also { syncObjectDirCreator ->

            dirList.forEach { syncObject ->

                syncObjectStateChanger.markAsBusy(syncObject.id)

                syncObjectDirCreator.createDir(syncObject, syncTask)
                    .onSuccess {
                        syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
                    }
                    .onFailure { throwable ->
                        ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                            syncObjectStateChanger.markAsError(syncObject.id, errorMsg)
                            Log.e(TAG, errorMsg, throwable)
                        }
                    }
            }
        }
    }

    companion object {
        val TAG: String = SyncTaskDirsCreator::class.java.simpleName
    }
}