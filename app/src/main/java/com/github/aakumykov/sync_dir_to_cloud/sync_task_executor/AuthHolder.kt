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

    suspend fun getSourceCloudAuth(syncTask: SyncTask): CloudAuth? {
        if (null == _sourceCloudAuth)
            _sourceCloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId)
        return _sourceCloudAuth
    }

    suspend fun getTargetCloudAuth(syncTask: SyncTask): CloudAuth? {
        if (null == _targetCloudAuth)
            _targetCloudAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId)
        return _targetCloudAuth
    }

    suspend fun getSourceAuthToken(syncTask: SyncTask): String?
        = getSourceCloudAuth(syncTask)?.authToken

    suspend fun getTargetAuthToken(syncTask: SyncTask): String?
        = getTargetCloudAuth(syncTask)?.authToken
}