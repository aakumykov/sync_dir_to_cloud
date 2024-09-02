package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.kotlin_playground.counting_buffered_streams.CountingBufferedInputStream
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.CancelHolder
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import com.github.aakumykov.sync_dir_to_cloud.utils.counting_buffered_streams.DelayedInputStream
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID
import kotlin.coroutines.coroutineContext

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
class SyncObjectFileCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressCallbackCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val cancelHolder: CancelHolder,
) {
    private var lastProgressValue: Float = 0f

    suspend fun copySyncObject(
        operationId: String,
        absoluteSourceFilePath: String,
        absoluteTargetFilePath: String,
        progressCalculator: ProgressCalculator,
        overwriteIfExists: Boolean = true,
        onProgressChanged: suspend (Int) -> Unit
    )
    : Result<String> {

        try {
            val sourceFileStream: InputStream = sourceFileStreamSupplier.getSourceFileStream(absoluteSourceFilePath).getOrThrow()

            val countingInputStream = DelayedInputStream(
                100L,
                inputStream = sourceFileStream,
                coroutineScope = CoroutineScope(Dispatchers.IO),
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

            val operationScope = CoroutineScope(coroutineContext).apply {
                try {
//                    countingInputStream.use {
                        cloudWriter.putFile(countingInputStream, absoluteTargetFilePath, overwriteIfExists)
//                    }
                }
                catch (e: CancellationException) {
                    Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
                }
            }

            cancelHolder.putCancelHandler(operationId, operationScope)

            return Result.success(absoluteTargetFilePath)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
        finally {
//            cancelHolder.removeCancelHandler(operationId)
        }
    }

    companion object {
        val TAG: String = SyncObjectFileCopier::class.java.simpleName
    }
}