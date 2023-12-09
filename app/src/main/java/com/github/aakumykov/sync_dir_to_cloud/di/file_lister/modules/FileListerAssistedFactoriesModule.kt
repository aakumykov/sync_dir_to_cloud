package com.github.aakumykov.sync_dir_to_cloud.di.file_lister.modules

import com.github.aakumykov.sync_dir_to_cloud.di.file_lister.annotations.KeyStorageType
import com.github.aakumykov.interfaces.StorageType
import com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories.FileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories.LocalFileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories.YandexDiskFileListerAssistedFactory
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