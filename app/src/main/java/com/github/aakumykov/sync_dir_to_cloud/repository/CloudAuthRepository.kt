package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.CloudAuthLocalDataSource
import javax.inject.Inject

class CloudAuthRepository @Inject constructor(
    private val localDataSource: CloudAuthLocalDataSource
)
    : CloudAuthReader, CloudAuthAdder, CloudAuthChecker
{
    override suspend fun addCloudAuth(cloudAuth: CloudAuth)
        = localDataSource.add(cloudAuth)

    override suspend fun listCloudAuth(): LiveData<List<CloudAuth>>
        = localDataSource.listCloudAuth()

    override suspend fun getCloudAuth(id: String): CloudAuth
        = localDataSource.get(id)

    override suspend fun hasAuthWithName(name: String): Boolean
        = localDataSource.hasAuthWithName(name)
}