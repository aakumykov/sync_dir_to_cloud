package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

@AppScope
class AuthHolder @Inject constructor(
    private val cloudAuthReader: CloudAuthReader
) {
    private var _sourceCloudAuth: CloudAuth? = null
    private var _targetCloudAuth: CloudAuth? = null


    suspend fun getSourceAuthToken(syncTask: SyncTask): String?
            = getSourceCloudAuth(syncTask)?.authToken


    // TODO: Предоставлять сюда подобъект SyncTask
    /*interface AuthTokenSupplier {
        val sourceAuthId: String?
        val targetAuthId: String?
    }*/

    suspend fun getTargetAuthToken(syncTask: SyncTask): String?
            = getTargetCloudAuth(syncTask)?.authToken


    private suspend fun getSourceCloudAuth(syncTask: SyncTask): CloudAuth? {
        if (null == _sourceCloudAuth)
            _sourceCloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId)
        return _sourceCloudAuth
    }

    private suspend fun getTargetCloudAuth(syncTask: SyncTask): CloudAuth? {
        if (null == _targetCloudAuth)
            _targetCloudAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId)
        return _targetCloudAuth
    }
}