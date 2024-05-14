package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.LocalStorageWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.factory_and_creator.StorageWriterFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.YandexStorageWriter
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface StorageWriterAssistedFactoriesModule3 {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalStorageWriterFactory3(factory: LocalStorageWriter.Factory): StorageWriterFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexStorageWriterFactory3(factory: YandexStorageWriter.Factory): StorageWriterFactory
}