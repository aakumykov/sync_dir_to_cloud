package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.local.LocalSourceFileStreamSupplierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.yandex_disk.YandexSourceFileStreamSupplierAssistedFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface SourceFileStreamSupplierAssistedFactoriesModule {

    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    @Binds
    fun bindLocalSourceFileStreamSupplierAssistedFactory(
        localSourceFileStreamSupplierAssistedFactory: LocalSourceFileStreamSupplierAssistedFactory
    ): SourceFileStreamSupplierAssistedFactory

    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    @Binds
    fun bindYandexSourceFileStreamSupplierAssistedFactory(
        yandexSourceFileStreamSupplierAssistedFactory: YandexSourceFileStreamSupplierAssistedFactory
    ): SourceFileStreamSupplierAssistedFactory
}