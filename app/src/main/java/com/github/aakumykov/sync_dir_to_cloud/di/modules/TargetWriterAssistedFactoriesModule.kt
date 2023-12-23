package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.LocalTargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.YandexTargetWriter
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TargetWriterAssistedFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun providesLocalAssistedFactory(factory: LocalTargetWriter.Factory): TargetWriterAssistedFactory

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun providesYandexAssistedFactory(factory: YandexTargetWriter.Factory): TargetWriterAssistedFactory

//    @Binds
//    @IntoMap
//    @KeyStorageType(StorageType.GOOGLE)
//    fun providesGoogleAssistedFactory(factory: GoogleTargetWriter.Factory): TargetWriterAssistedFactory
}
