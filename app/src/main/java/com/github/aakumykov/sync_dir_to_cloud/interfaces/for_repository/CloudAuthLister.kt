package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

interface CloudAuthLister {
    suspend fun listCloudAuth(): LiveData<List<CloudAuth>>
}