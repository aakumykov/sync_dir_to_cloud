package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask

interface FullSyncTaskReader {
    suspend fun getFullSyncTask(id: String): FullSyncTask
}