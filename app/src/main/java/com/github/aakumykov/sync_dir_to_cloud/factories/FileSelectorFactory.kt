package com.github.aakumykov.sync_dir_to_cloud.factories

import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelectorFragment
import com.github.aakumykov.file_lister_navigator_selector.local_file_selector.LocalFileSelectorFragment
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.EndpointType
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelectorFragment

class FileSelectorFactory {

    fun create(
        fragmentResultKey: String,
        sourceStorageType: StorageType,
        cloudAuth: CloudAuth
    )
        : FileSelectorFragment<SimpleSortingMode>
    {
        return when(sourceStorageType) {
            StorageType.LOCAL -> LocalFileSelectorFragment.create(fragmentResultKey)

            StorageType.YANDEX_DISK -> YandexDiskFileSelectorFragment.create(
                fragmentResultKey,
                cloudAuth.authToken
            )
        }
    }
}