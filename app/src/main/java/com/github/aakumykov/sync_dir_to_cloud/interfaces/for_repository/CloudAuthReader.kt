package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.entities.CloudAuth

interface CloudAuthReader {
    suspend fun listCloudAuth(): LiveData<List<CloudAuth>>
    suspend fun getCloudAuth(id: String): CloudAuth
}