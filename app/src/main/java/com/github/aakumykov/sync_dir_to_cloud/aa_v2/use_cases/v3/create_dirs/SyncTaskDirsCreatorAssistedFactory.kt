package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import dagger.assisted.AssistedFactory

@AssistedFactory
interface SyncTaskDirsCreatorAssistedFactory {
    fun create(executionId: String): SyncTaskDirsCreator
}