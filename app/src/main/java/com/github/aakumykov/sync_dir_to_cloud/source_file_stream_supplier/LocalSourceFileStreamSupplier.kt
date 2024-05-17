package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.FileInputStream

class LocalSourceFileStreamSupplier @AssistedInject constructor(
    @Assisted private val unusedTaskId: String,
) : SourceFileStreamSupplier
{
    override suspend fun getSourceFileStream(absolutePath: String): Result<FileInputStream> {
        return App.cloudReadersComponent.getLocalCloudReader().getFileInputStream(absolutePath)
    }

    @AssistedFactory
    interface Factory : SourceFileStreamSupplierFactory {
        override fun create(taskId: String): LocalSourceFileStreamSupplier
    }
}
