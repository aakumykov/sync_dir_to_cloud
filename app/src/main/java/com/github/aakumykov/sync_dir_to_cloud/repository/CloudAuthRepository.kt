package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import javax.inject.Inject

class CloudAuthRepository @Inject constructor(
    private val cloudAuthDAO: CloudAuthDAO
)
    : CloudAuthReader, CloudAuthAdder, CloudAuthChecker
{
    override suspend fun addCloudAuth(cloudAuth: CloudAuth)
        = cloudAuthDAO.add(cloudAuth)

    override suspend fun listCloudAuth(): LiveData<List<CloudAuth>>
        = cloudAuthDAO.list()

    override suspend fun getCloudAuth(id: String?): CloudAuth? {
        return id?.let { cloudAuthDAO.get(id) }
    }

    override fun getCloudAuthSimple(id: String?): CloudAuth? {
        return id?.let { cloudAuthDAO.getSimple(id) }
    }

    override suspend fun hasAuthWithName(name: String): Boolean
        = cloudAuthDAO.hasName(name)
}