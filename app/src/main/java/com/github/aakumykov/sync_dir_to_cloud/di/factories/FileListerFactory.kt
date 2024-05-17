package com.github.aakumykov.sync_dir_to_cloud.di.factories

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.local_file_lister.LocalFileLister
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import dagger.assisted.AssistedFactory

interface FileListerFactory {
    fun createFileLister(authToken: String): FileLister<SimpleSortingMode>
}

@AssistedFactory
interface LocalFileListerFactory: FileListerFactory {
    override fun createFileLister(authToken: String): LocalFileLister
}

@AssistedFactory
interface YandexDiskFileListerFactory: FileListerFactory {
    override fun createFileLister(authToken: String): YandexDiskFileLister
}