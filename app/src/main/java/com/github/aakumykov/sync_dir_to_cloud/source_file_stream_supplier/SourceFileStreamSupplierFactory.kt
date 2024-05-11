package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.local.LocalSourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.yandex_disk.YandexSourceFileStreamSupplier
import javax.inject.Inject

class SourceFileStreamSupplierFactory @Inject constructor(
    private val map: Map<StorageType, SourceFileStreamSupplierAssistedFactory>
) {
    fun create(authToken: String, storageType: StorageType): SourceFileStreamSupplier? {
        return map[storageType]?.create(authToken)
    }
}
