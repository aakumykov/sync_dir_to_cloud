package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.LocalCloudWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.YandexCloudWriterAssistedFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CloudWriterFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalCloudWriterAssistedFactory(
        localCloudWriterAssistedFactory: LocalCloudWriterAssistedFactory
    ): CloudWriterAssistedFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexDiskCloudWriterAssistedFactory(
        yandexDiskCloudWriterAssistedFactory: YandexCloudWriterAssistedFactory
    ): CloudWriterAssistedFactory
}
