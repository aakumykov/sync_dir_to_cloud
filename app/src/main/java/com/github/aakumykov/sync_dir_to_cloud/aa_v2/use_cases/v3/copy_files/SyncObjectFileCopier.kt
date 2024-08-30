package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.actualSize
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.round
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.CountingBufferedInputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
// TODO: внедрять объект-считатель прогресса
class SyncObjectFileCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressCallbackCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private var lastProgressValue: Float = 0f

    suspend fun copySyncObject(
        syncObject: SyncObject,
        absoluteSourceFilePath: String,
        absoluteTargetFilePath: String,
        overwriteIfExists: Boolean = true,
        onProgressChanged: suspend (Int) -> Unit
    )
    : Result<String> {

        try {
            val sourceFileStream: InputStream = sourceFileStreamSupplier.getSourceFileStream(absoluteSourceFilePath).getOrThrow()

            val countingInputStream = CountingBufferedInputStream(sourceFileStream) { readCount ->

                // Если размер файла 0, приходит "считано 0" и прогресс сразу становится 100%.
                val progress = if (0L == syncObject.actualSize) 1.0f
                               else (1f*readCount / syncObject.actualSize).round(2)

                val progressAsPartOf100 = Math.round(progress * 100)

                // Реализация пропуска повторяющихся значений
                if (lastProgressValue != progress) {
                    lastProgressValue = progress
                    CoroutineScope(progressCallbackCoroutineDispatcher).launch {
                        onProgressChanged.invoke(progressAsPartOf100)
                    }
                }
            }

            cloudWriter.putFile(countingInputStream, absoluteTargetFilePath, overwriteIfExists)

            return Result.success(absoluteTargetFilePath)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }

    companion object {
        val TAG: String = SyncObjectFileCopier::class.java.simpleName
    }
}