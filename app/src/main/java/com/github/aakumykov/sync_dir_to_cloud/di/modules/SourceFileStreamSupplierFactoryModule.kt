package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplierFactory
import dagger.Module
import dagger.Provides

@Module
class SourceFileStreamSupplierFactoryModule {

    @Provides
    fun provideSourceFileStreamSupplierFactory(
        assistedFactoriesMap: Map<StorageType, @JvmSuppressWildcards SourceFileStreamSupplierAssistedFactory>
    ): SourceFileStreamSupplierFactory {
        return SourceFileStreamSupplierFactory(assistedFactoriesMap)
    }
}