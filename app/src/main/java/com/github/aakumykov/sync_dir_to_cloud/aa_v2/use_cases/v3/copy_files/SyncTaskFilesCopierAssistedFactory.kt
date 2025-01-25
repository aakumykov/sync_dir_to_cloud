package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import dagger.assisted.AssistedFactory

@AssistedFactory
interface SyncTaskFilesCopierAssistedFactory {
    fun create(executionId: String, chunkSize: Int = 3): SyncTaskFilesCopier
}