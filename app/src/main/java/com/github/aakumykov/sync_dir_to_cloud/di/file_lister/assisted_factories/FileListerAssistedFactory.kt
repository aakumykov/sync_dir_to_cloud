package com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.local_file_lister.LocalFileLister
import com.github.aakumykov.yandex_disk_file_lister.YandexDiskFileLister
import dagger.assisted.AssistedFactory

interface FileListerAssistedFactory {
    fun create(authToken: String): FileLister
}

@AssistedFactory
interface LocalFileListerAssistedFactory : FileListerAssistedFactory {
    override fun create(authToken: String): LocalFileLister {
        return LocalFileLister(authToken)
    }
}

@AssistedFactory
interface YandexDiskFileListerAssistedFactory : FileListerAssistedFactory {
    override fun create(authToken: String): YandexDiskFileLister {
        return YandexDiskFileLister(authToken)
    }
}