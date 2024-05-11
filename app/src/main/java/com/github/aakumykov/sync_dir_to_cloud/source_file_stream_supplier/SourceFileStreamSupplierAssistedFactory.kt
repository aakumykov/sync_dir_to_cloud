package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

interface SourceFileStreamSupplierAssistedFactory {
    fun create(authToken: String): SourceFileStreamSupplier
}
