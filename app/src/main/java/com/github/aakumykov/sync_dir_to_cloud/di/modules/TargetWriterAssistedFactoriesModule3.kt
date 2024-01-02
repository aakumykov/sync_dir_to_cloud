package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.LocalTargetWriter3
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.TargetWriterFactory3
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.YandexTargetWriter3
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TargetWriterAssistedFactoriesModule3 {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalTargetWriterFactory3(factory: LocalTargetWriter3.Factory): TargetWriterFactory3

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexTargetWriterFactory3(factory: YandexTargetWriter3.Factory): TargetWriterFactory3
}