package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream

/**
 * Копирует данные из файла, расположенному по пути источника в файл по пути приёмника.
 * Читает данные из потока чтения (который получает от [SourceFileStreamSupplier]-а.
 * Пишет в файл с помощью [CloudWriter]-а.
 * Возвращает данные о количестве переданных байт через коллбек.
 */
class SyncObjectDataCopier(
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressCallbackCoroutineScope: CoroutineScope,
    private val progressCallbackCoroutineDispatcher: CoroutineDispatcher,
) {
    private var lastProgressValue: Float = 0f

    suspend fun copyDataFromPathToPathResult(
        absoluteSourceFilePath: String,
        absoluteTargetFilePath: String,
        progressCalculator: ProgressCalculator,
        overwriteIfExists: Boolean = true,
        onProgressChanged: suspend (Int) -> Unit
    )
    : Result<String> {

        try {
            copyDataFromPathToPath(
                absoluteSourceFilePath = absoluteSourceFilePath,
                absoluteTargetFilePath = absoluteTargetFilePath,
                progressCalculator = progressCalculator,
                overwriteIfExists = overwriteIfExists,
                onProgressChanged = onProgressChanged,
            )
            return Result.success(absoluteTargetFilePath)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }


    suspend fun copyDataFromPathToPath(
        absoluteSourceFilePath: String,
        absoluteTargetFilePath: String,
        progressCalculator: ProgressCalculator,
        overwriteIfExists: Boolean = true,
        onProgressChanged: (suspend (Int) -> Unit)? = null,
    ) {
        return suspendCancellableCoroutine { cancellableContinuation ->

            val sourceFileStream: InputStream = sourceFileStreamSupplier
                .getSourceFileStreamSimple(absoluteSourceFilePath)
                .getOrThrow()

            cancellableContinuation.invokeOnCancellation { cause: Throwable? ->
                sourceFileStream.close()
            }

            cloudWriter.putStream(
                inputStream = sourceFileStream,
                targetPath = absoluteTargetFilePath,
                overwriteIfExists = overwriteIfExists
            ) { writtenBytesCount: Long ->
                onProgressChanged?.also { progressCallback ->
                    progressCallbackCoroutineScope.launch (progressCallbackCoroutineDispatcher) {
                        val progress = progressCalculator.calcProgress(writtenBytesCount)
                        if (lastProgressValue != progress) { // Реализация пропуска повторяющихся значений
                            lastProgressValue = progress
                            progressCallback.invoke(progressCalculator.progressAsPartOf100(progress))
                        }
                    }
                }
            }
        }
    }


    companion object {
        val TAG: String = SyncObjectDataCopier::class.java.simpleName
    }
}