package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.di.factories.FileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.factories.LocalFileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.factories.YandexDiskFileListerAssistedFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FileListerAssistedFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindsLocalFileLister(assistedFactory: LocalFileListerAssistedFactory): FileListerAssistedFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindsYandexDiskFileLister(assistedFactory: YandexDiskFileListerAssistedFactory): FileListerAssistedFactory
}