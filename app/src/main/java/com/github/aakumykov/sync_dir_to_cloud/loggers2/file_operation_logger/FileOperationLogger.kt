package com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger

import kotlinx.coroutines.CancellationException
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.CommonFileInstructionsProcessor

/**
 * Интерфейс создан для того, чтобы делегировать его
 * функционал в классе [CommonFileInstructionsProcessor].
 * Не очень красиво, зато позволяет делегировать и потенциально
 * добавлять другие типы журналирования.
 */
interface FileOperationLogger {
    suspend fun logStarted(baseInfo: DatabaseFileOperationLogger.LogBaseInfo, jobId: String)
    suspend fun logFinished(baseInfo: DatabaseFileOperationLogger.LogBaseInfo)
    suspend fun logCancelled(baseInfo: DatabaseFileOperationLogger.LogBaseInfo, e: CancellationException)
    suspend fun logError(baseInfo: DatabaseFileOperationLogger.LogBaseInfo, t: Throwable)
}