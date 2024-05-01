package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream.SourceFileStreamSupplier

class SFSSFactory {
    companion object {
        fun create(
            taskId: String,
            storageType: StorageType
        )
            : SourceFileStreamSupplier
        {
            return when(storageType) {
                StorageType.LOCAL -> LocalSFSS()
                StorageType.YANDEX_DISK -> YandexSFSS(taskId)
            }
        }
    }

}
