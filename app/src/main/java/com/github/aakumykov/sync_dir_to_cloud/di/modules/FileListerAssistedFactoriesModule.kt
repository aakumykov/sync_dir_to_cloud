package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.di.factories.FileListerFactory
import com.github.aakumykov.sync_dir_to_cloud.di.factories.LocalFileListerFactory
import com.github.aakumykov.sync_dir_to_cloud.di.factories.YandexDiskFileListerFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FileListerAssistedFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindsLocalFileLister(assistedFactory: LocalFileListerFactory): FileListerFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindsYandexDiskFileLister(assistedFactory: YandexDiskFileListerFactory): FileListerFactory
}