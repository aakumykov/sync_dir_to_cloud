package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.cloud_auth

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthReader
import javax.inject.Inject

class CloudAuthManagingUseCase @Inject constructor(
    val cloudAuthReader: CloudAuthReader,
    val cloudAuthAdder: CloudAuthAdder, // TODO: creator-deleter
    val cloudAuthChecker: CloudAuthChecker
) {
    suspend fun addCloudAuth(cloudAuth: CloudAuth) {
        cloudAuthAdder.addCloudAuth(cloudAuth)
    }

    suspend fun getCloudAuth(id: String): CloudAuth? {
        return cloudAuthReader.getCloudAuth(id)
    }

    suspend fun hasCloudAuth(name: String): Boolean {
        return cloudAuthChecker.hasAuthWithName(name)
    }
}