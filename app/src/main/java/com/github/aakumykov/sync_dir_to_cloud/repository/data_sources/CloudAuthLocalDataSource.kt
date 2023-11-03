package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.repository.room.CloudAuthDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO: @область видимости фрагмента
class CloudAuthLocalDataSource @Inject constructor(private val cloudAuthDAO: CloudAuthDAO) {

    suspend fun listCloudAuth(): LiveData<List<CloudAuth>> {
        return withContext(Dispatchers.IO) {
            cloudAuthDAO.list()
        }
    }

    suspend fun add(cloudAuth: CloudAuth)
        = withContext(Dispatchers.IO) { cloudAuthDAO.add(cloudAuth) }

    suspend fun hasAuthWithName(name: String): Boolean
        = withContext(Dispatchers.IO) { cloudAuthDAO.hasName(name) }

    suspend fun get(id: String): CloudAuth
        = withContext(Dispatchers.IO) { cloudAuthDAO.get(id) }
}