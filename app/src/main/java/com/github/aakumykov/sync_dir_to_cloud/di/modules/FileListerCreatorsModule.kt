package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.factories.file_lister.FileListerCreator
import com.github.aakumykov.sync_dir_to_cloud.factories.file_lister.LocalFileListerCreator
import com.github.aakumykov.sync_dir_to_cloud.factories.file_lister.YandexDiskFileListerCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FileListerCreatorsModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalFileListerCreator(localFileListerCreator: LocalFileListerCreator): FileListerCreator

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexDiskFileListerCreator(yandexDiskFileListerCreator: YandexDiskFileListerCreator): FileListerCreator
}