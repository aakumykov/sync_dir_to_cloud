package com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileListerFactory
import com.github.aakumykov.yandex_disk_file_lister.YandexDiskFileLister
import dagger.assisted.AssistedFactory

@AssistedFactory
interface YandexDiskFileListerAssistedFactory : FileListerFactory {
    override fun create(authToken: String): YandexDiskFileLister {
        return YandexDiskFileLister(authToken)
    }
}