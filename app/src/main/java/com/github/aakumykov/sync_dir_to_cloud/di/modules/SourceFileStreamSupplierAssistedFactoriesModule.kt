package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.KeyStorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.LocalSourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.YandexSourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface SourceFileStreamSupplierAssistedFactoriesModule {

    @IntoMap
    @KeyStorageType(StorageType.LOCAL)
    @Binds
    fun bindLocalSourceFileStreamSupplierAssistedFactory(
        localSourceFileStreamSupplierAssistedFactory: LocalSourceFileStreamSupplier.Factory
    ): SourceFileStreamSupplierFactory

    @IntoMap
    @KeyStorageType(StorageType.YANDEX_DISK)
    @Binds
    fun bindYandexSourceFileStreamSupplierAssistedFactory(
        yandexSourceFileStreamSupplierAssistedFactory: YandexSourceFileStreamSupplier.Factory
    ): SourceFileStreamSupplierFactory
}