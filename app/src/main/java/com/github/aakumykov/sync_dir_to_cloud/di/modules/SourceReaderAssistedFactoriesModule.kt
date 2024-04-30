package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.local_source_reader.LocalSourceReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.yandex_source_reader.YandexSourceReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReaderAssistedFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface SourceReaderAssistedFactoriesModule {

    @Binds
    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    fun bindLocalSourceReader(factory: LocalSourceReaderAssistedFactory): SourceReaderAssistedFactory
    
    @Binds
    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    fun bindYandexSourceReader(factory: YandexSourceReaderAssistedFactory): SourceReaderAssistedFactory
}