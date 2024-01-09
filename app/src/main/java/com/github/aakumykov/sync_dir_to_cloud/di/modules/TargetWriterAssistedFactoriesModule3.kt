package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.LocalTargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.factory_and_creator.TargetWriterFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.YandexTargetWriter
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TargetWriterAssistedFactoriesModule3 {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalTargetWriterFactory3(factory: LocalTargetWriter.Factory): TargetWriterFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexTargetWriterFactory3(factory: YandexTargetWriter.Factory): TargetWriterFactory
}