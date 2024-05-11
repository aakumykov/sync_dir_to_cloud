package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.local

import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplierAssistedFactory
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LocalSourceFileStreamSupplierAssistedFactory : SourceFileStreamSupplierAssistedFactory {
    override fun create(authToken: String): LocalSourceFileStreamSupplier
}