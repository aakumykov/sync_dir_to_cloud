package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.CancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import com.github.aakumykov.sync_dir_to_cloud.utils.counting_buffered_streams.CancelableInputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
class SyncObjectFileCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressCallbackCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val cancellationMarker: CancelableInputStream.CancellationMarker,
    private val cancellationHolder: CancellationHolder,
) {
    private var lastProgressValue: Float = 0f

    suspend fun copySyncObject(
        absoluteSourceFilePath: String,
        absoluteTargetFilePath: String,
        progressCalculator: ProgressCalculator,
        overwriteIfExists: Boolean = true,
        cancellationMarker: CancelableInputStream.CancellationMarker,
        executionId: String,
        onProgressChanged: suspend (Int) -> Unit
    )
    : Result<String> {

        try {
            val sourceFileStream: InputStream = sourceFileStreamSupplier.getSourceFileStream(absoluteSourceFilePath).getOrThrow()

            val inputStream = CancelableInputStream(
                inputStream = sourceFileStream,
                cancellationMarker =cancellationMarker,
            ) { readedCount ->

                val progress = progressCalculator.calcProgress(readedCount)

                // Реализация пропуска повторяющихся значений
                if (lastProgressValue != progress) {
                    lastProgressValue = progress
                    CoroutineScope(progressCallbackCoroutineDispatcher).launch {
                        onProgressChanged.invoke(progressCalculator.progressAsPartOf100(progress))
                    }
                }
            }

            cancellationHolder.putCancellationMarker(
                CancellationHolder.idFor()
            )

            cloudWriter.putFile(inputStream, absoluteTargetFilePath, overwriteIfExists)

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