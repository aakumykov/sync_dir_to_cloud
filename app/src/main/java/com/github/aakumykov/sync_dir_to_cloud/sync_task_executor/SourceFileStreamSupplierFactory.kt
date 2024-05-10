package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream.SourceFileStreamSupplier
import javax.inject.Inject

class SourceFileStreamSupplierFactory @Inject constructor(

) {
    companion object {

        fun create(taskId: String, storageType: StorageType): SourceFileStreamSupplier {
            return when(storageType) {
                StorageType.LOCAL -> LocalSourceFileStreamSupplier()
                StorageType.YANDEX_DISK -> YandexSourceFileStreamSupplier(taskId)
            }
        }
    }

}
