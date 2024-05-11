package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier

interface SourceFileStreamSupplierFactory {
    fun create(authToken: String): SourceFileStreamSupplier
}
