package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader.CloudReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader.LocalCloudReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader.YandexDiskCloudReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CloudReaderFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalCloudReaderFactory(localCloudReaderFactory: LocalCloudReaderFactory): CloudReaderFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexDiskCloudReaderFactory(yandexDiskCloudReaderFactory: YandexDiskCloudReaderFactory): CloudReaderFactory
}