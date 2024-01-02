package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.cloud_writer.YandexCloudWriter
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterFactory
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.LocalCloudWriterFactory
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.YandexCloudWriterFactory
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface CloudWriterAssistedFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalCloudWriterFactory(localCloudWriterFactory: LocalCloudWriterFactory): CloudWriterFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexCloudWriterFactory(yandexCloudWriterFactory: YandexCloudWriterFactory): CloudWriterFactory
}
