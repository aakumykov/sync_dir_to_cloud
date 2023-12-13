package com.github.aakumykov.sync_dir_to_cloud.di.factories

import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskFilesPreparer
import dagger.assisted.AssistedFactory

@AssistedFactory
interface SyncTaskFilesPreparerAssistedFactory {
    fun create(recursiveDirReader: RecursiveDirReader): SyncTaskFilesPreparer
}