package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.LocalSourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.YandexSourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream.SourceFileStreamSupplier
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class SourceFileStreamSuppliersModule {

    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    @Provides
    fun provideLocalSourceFileStreamSupplier(): dagger.Lazy<SourceFileStreamSupplier> {
        return dagger.Lazy { LocalSourceFileStreamSupplier() }
    }

    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    @Provides
    fun provideYandexDiskSourceFileStreamSupplier(): dagger.Lazy<SourceFileStreamSupplier> {
        return dagger.Lazy { YandexSourceFileStreamSupplier() }
    }
}