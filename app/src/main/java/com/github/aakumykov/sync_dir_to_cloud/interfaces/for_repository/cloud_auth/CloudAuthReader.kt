package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

interface CloudAuthReader {
    suspend fun listCloudAuth(): LiveData<List<CloudAuth>>
    suspend fun getCloudAuth(id: String): CloudAuth
    fun getCloudAuthBlocking(authId: String): CloudAuth
}