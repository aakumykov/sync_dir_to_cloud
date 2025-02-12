package com.github.aakumykov.sync_dir_to_cloud.in_target_existence_checker

import android.util.Log
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InTargetExistenceChecker @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudReaderGetter: CloudReaderGetter,
    private val syncObjectUpdater: SyncObjectUpdater,
    private val syncObjectStateChanger: SyncObjectStateChanger,
) {
    private var cloudReader: CloudReader? = null

    private suspend fun cloudReader(): CloudReader? {
        if (null == cloudReader) {
            cloudReader = cloudReaderGetter.getCloudReader(
                syncTask.targetStorageType,
                cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken
            )
        }
        return cloudReader
    }

    // FIXME: что делать с null-абельностью targetPath
    suspend fun checkObjectExists(syncObject: SyncObject) {

        val objectId = syncObject.id
        syncObjectStateChanger.setTargetReadingState(objectId, ExecutionState.RUNNING)

        cloudReader()
            ?.fileExists(syncObject.absolutePathIn(syncTask.targetPath!!))
            ?.onSuccess { isExists ->
                // TODO: транзакция?
                syncObjectStateChanger.setTargetReadingState(objectId, ExecutionState.SUCCESS)
                syncObjectUpdater.setIsExistsInTarget(objectId, isExists)
            }
            ?.onFailure {
                ExceptionUtils.getErrorMessage(it).also { errorMsg ->
                    // TODO: транзакция?
                    syncObjectStateChanger.setTargetReadingState(objectId, ExecutionState.ERROR, errorMsg)
                    syncObjectUpdater.setIsExistsInTarget(objectId, false)
                    Log.e(tag, errorMsg, it)
                }
            }
    }


    @AssistedFactory
    interface Factory {
        fun create(syncTask: SyncTask): InTargetExistenceChecker
    }
}
