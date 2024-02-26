package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

// TODO: можно сделать интерфейсом или перенести сюда SyncObjectReader
abstract class ChangesDetectionStrategy {

    abstract suspend fun detectItemModification(
        taskId: String,
        sourcePath: String,
        newFsItem: FSItem
    ): ModificationState

    abstract suspend fun detectItemModification(existingSyncObject: SyncObject?, newFsItem: FSItem): ModificationState
}