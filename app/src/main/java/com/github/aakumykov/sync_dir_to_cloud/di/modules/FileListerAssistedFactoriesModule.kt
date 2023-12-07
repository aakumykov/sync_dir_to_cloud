package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories.LocalFileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories.QwertyFileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories.YandexDiskFileListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileListerFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FileListerAssistedFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindsLocalFileListerAssistedFactory(localFileListerAssistedFactory: LocalFileListerAssistedFactory): FileListerFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindsYandexFileListerAssistedFactory(yandexFileListerAssistedFactory: YandexDiskFileListerAssistedFactory): FileListerFactory


    @Binds
    @IntoMap
    @KeyStorageType(StorageType.QWERTY)
    fun bindsQwertyFileListerAssistedFactory(qwertyFileListerAssistedFactory: QwertyFileListerAssistedFactory): FileListerFactory
}