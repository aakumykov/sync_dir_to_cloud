package com.github.aakumykov.sync_dir_to_cloud.factories.file_selector

import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelector

class FileSelectorFactory {

    fun create(
        fragmentResultKey: String,
        sourceStorageType: StorageType,
        cloudAuth: CloudAuth
    )
        : FileSelector<SimpleSortingMode>
    {
        return when(sourceStorageType) {
            StorageType.LOCAL -> LocalFileSelector.create(fragmentResultKey)
            StorageType.YANDEX_DISK -> YandexDiskFileSelector.create(fragmentResultKey, cloudAuth.authToken)
        }
    }
}