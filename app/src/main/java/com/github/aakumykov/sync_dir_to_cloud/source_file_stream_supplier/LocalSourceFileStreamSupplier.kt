package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class LocalSourceFileStreamSupplier @AssistedInject constructor(
    @Assisted private val unusedTaskId: String,
) : SourceFileStreamSupplier
{
    init {
        Log.d(TAG, "init")
    }

    @Deprecated("Возвращать InputStream, как в контракте!")
    override suspend fun getSourceFileStream(absolutePath: String): Result<FileInputStream> {
        return getSourceFileStreamSimple(absolutePath)
    }

    @Deprecated("Возвращать InputStream, как в контракте!")
    override fun getSourceFileStreamSimple(absolutePath: String): Result<FileInputStream> {
        return try {
            Result.success(File(absolutePath).inputStream())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @AssistedFactory
    interface Factory : SourceFileStreamSupplierFactory {
        override fun create(taskId: String): LocalSourceFileStreamSupplier
    }

    companion object {
        val TAG: String = LocalSourceFileStreamSupplier::class.java.simpleName
    }
}
