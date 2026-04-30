package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettings
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.utils.BytesToHumanSizeFormatter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Методы этого класса обязаны быть "suspend",
 * чтобы реагировать на отмену закрытием потока.
 *
 * А вот коллбеки у них не-suspend. Причина - они используются в не-suspend коллбеках
 * внешних библиотек, которые не хочется переписывать.
 *
 * Для решения проблемы вызова suspend-функций в коллбеках этого класса,
 * нуужно применять scope в вышележащих методах.
 *
 * Для обработки отмены копирования пользователем класс
 * вынужден хранить состояние: объект Throwable, полученный
 * в коллбеке Coroutine.invokeOnCancellation.
 */
class StreamToFileWriter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
    private val appSettings: AppSettings,
) {
    private var receivedCancellationThrowable: Throwable? = null

    @Throws(StreamWriterCancelledException::class)
    suspend fun putStreamToTarget(inputStream: InputStream,
                                  filePath: String,
                                  overwriteIfExists: Boolean,
                                  progressCallback: ((transferredBytes: Long) -> Unit)? = null,
    ) {
        /*if (Random.nextInt(1,101) < 30)
            throw Exception("Случайная ошибка")*/

        Log.d(TAG, "putFileToTarget('$filePath', $overwriteIfExists)")
        putStreamReal(
            cloudWriterGetter.getTargetCloudWriter(syncTask),
            inputStream,
            filePath,
            overwriteIfExists,
            progressCallback
        )
    }


    @Throws(StreamWriterCancelledException::class)
    suspend fun putStreamToSource(
        inputStream: InputStream,
        filePath: String,
        overwriteIfExists: Boolean,
        progressCallback: ((transferredBytes: Long) -> Unit)? = null,
    ) {
        Log.d(TAG, "putFileToSource('$filePath')")
        putStreamReal(
            cloudWriterGetter.getSourceCloudWriter(syncTask),
            inputStream,
            filePath,
            overwriteIfExists,
            progressCallback
        )
    }


    @Throws(StreamWriterCancelledException::class)
    private suspend fun putStreamReal(
        cloudWriter: CloudWriter,
        inputStream: InputStream,
        filePath: String,
        overwriteIfExists: Boolean,
        progressCallback: ((transferredBytes: Long) -> Unit)? = null,
    ) {
        val dataTransferDelay = appSettings.fileTransferRetardationMs

        if (dataTransferDelay > 0)
            Log.d(TAG, "putStreamReal(), используется замедление копирования $dataTransferDelay мс.")
        else
            Log.d(TAG, "putStreamReal(), копирование без замедления.")

        return suspendCancellableCoroutine { cancellableContinuation ->

            cancellableContinuation.invokeOnCancellation {
                Log.d(TAG, "cancellableContinuation.invokeOnCancellation(): ${it?.errorMsgExtended}")
                receivedCancellationThrowable = it
                inputStream.close()
            }

            try {
                cloudWriter
                    .putStream(
                        inputStream = inputStream,
                        targetAbsolutePath = filePath,
                        overwriteIfExists = overwriteIfExists,
                        bufferSize = appSettings.streamCopyingBufferSize,
                        writingCallback = { progress ->

                            Log.d(TAG, "прогресс записи файла: ${BytesToHumanSizeFormatter.format(progress)}")

                            dataTransferDelay.also { delayMs ->
                                if (delayMs > 0)
                                    TimeUnit.MILLISECONDS.sleep(delayMs.toLong())
                            }

                            // TODO: вернуть это?
                            /*if (!cancellableContinuation.isActive)
                                return@putStream*/

                            progressCallback?.invoke(progress)

                        },
                        finishCallback = { _,_ ->
                            cancellableContinuation.resume(Unit)
                        },
                        requiredSpeedBytesPerSecondSupplier = { syncTask.speedBytesPerSecond }
                    )
            } catch (t: Throwable) {
                Log.e(TAG, t.errorMsg, t)

                if (receivedCancellationThrowable !is CancellationException) {
                    receivedCancellationThrowable = null
                    Log.d(TAG, "Перевыбрасываю исключение '${t.errorMsgExtended}'")
                    throw t
                }

            } finally {
                inputStream.close()
            }
        }
    }


    companion object {
        val TAG: String = StreamToFileWriter::class.java.simpleName
    }

    class StreamWriterCancelledException(message: String) : IOException(message)
}

@AssistedFactory
interface StreamToFileWriterAssistedFactory {
    fun create(syncTask: SyncTask): StreamToFileWriter
}