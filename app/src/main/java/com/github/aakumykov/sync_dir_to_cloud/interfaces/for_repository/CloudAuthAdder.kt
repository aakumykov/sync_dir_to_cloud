package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import com.github.aakumykov.entities.CloudAuth

interface CloudAuthAdder {
    suspend fun addCloudAuth(cloudAuth: CloudAuth)
}