package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.kotlin_playground.counting_buffered_streams.CountingBufferedInputStream
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * Копирует данные из файла, расположенному по пути источника в файл по пути приёмника.
 * Читает данные из потока чтения (который получает от [SourceFileStreamSupplier]-а.
 * Пишет в файл с помощью [CloudWriter]-а.
 * Возвращает данные о количестве переданных байт через коллбек.
 */
class StreamToFileDataCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressCallbackCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private var lastProgressValue: Float = 0f

    suspend fun copySyncObject(
        absoluteSourceFilePath: String,
        absoluteTargetFilePath: String,
        progressCalculator: ProgressCalculator,
        overwriteIfExists: Boolean = true,
        onProgressChanged: suspend (Int) -> Unit
    )
    : Result<String> {

        try {
            val sourceFileStream: InputStream = sourceFileStreamSupplier.getSourceFileStream(absoluteSourceFilePath).getOrThrow()

            val countingInputStream = CountingBufferedInputStream(sourceFileStream) { readedCount ->

                val progress = progressCalculator.calcProgress(readedCount)

                // Реализация пропуска повторяющихся значений
                if (lastProgressValue != progress) {
                    lastProgressValue = progress
                    CoroutineScope(progressCallbackCoroutineDispatcher).launch {
                        onProgressChanged.invoke(progressCalculator.progressAsPartOf100(progress))
                    }
                }
            }

            cloudWriter.putStream(
                inputStream = countingInputStream,
                targetPath = absoluteTargetFilePath,
                overwriteIfExists = overwriteIfExists
            ) { writtenBytesCount: Long ->
                Log.d(TAG, "записано байт: $writtenBytesCount")
            }

            return Result.success(absoluteTargetFilePath)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }

    companion object {
        val TAG: String = StreamToFileDataCopier::class.java.simpleName
    }
}