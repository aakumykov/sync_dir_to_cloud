package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.local

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class LocalSourceFileStreamSupplier @AssistedInject constructor(
    @Assisted private val dummyAuthToken: String
) : SourceFileStreamSupplier
{
    init {
        Log.d("LSFSS", "init")
    }

    override suspend fun getSourceFileStream(absolutePath: String): Result<FileInputStream> {
        return try {
            Result.success(File(absolutePath).inputStream())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
