package com.github.aakumykov.sync_dir_to_cloud.factories.file_lister_creator

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.local_file_lister.LocalFileLister
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import javax.inject.Inject

interface FileListerCreator {
    fun createFileLister(authToken: String): FileLister<SimpleSortingMode>
}

class LocalFileListerCreator @Inject constructor(): FileListerCreator {
    override fun createFileLister(authToken: String): FileLister<SimpleSortingMode> {
        return LocalFileLister(authToken)
    }
}

class YandexDiskFileListerCreator @Inject constructor(): FileListerCreator {
    override fun createFileLister(authToken: String): FileLister<SimpleSortingMode> {
        return YandexDiskFileLister(authToken)
    }
}