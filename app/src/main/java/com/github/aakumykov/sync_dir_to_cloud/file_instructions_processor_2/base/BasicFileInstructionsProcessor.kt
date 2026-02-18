package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose.ProgressHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class BasicFileInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val fileOperationLoggerFactory: FileOperationLoggerAssistedFactory,
    private val operationJobsHolder: OperationJobsHolder,
) {
    fun process(
        scope: CoroutineScope,
        @StringRes operationName: Int,
        logItemId: String,
        firstItem: String?,
        secondItem: String?,
        codeBlock: suspend () -> Unit
    ): Job {
        val jobId = newRandomId // TODO: возможно, не нужно

        return runInCoroutineExtended(
            scope = scope,
            onStart = { job ->
                operationJobsHolder.addJob(logItemId, job)
                ProgressHolder.addProgressState(logItemId)

                fileOperationLogger.logStarted(
                    logItemId = logItemId,
                    jobId = jobId,
                    operationName = operationName,
                    firstItem = firstItem,
                    secondItem = secondItem
                )
            },
            onFinish = {
                fileOperationLogger.logFinished(
                    logItemId = logItemId,
                    operationName,
                    firstItem,
                    secondItem
                )
            },
            onCancel = { e ->
                fileOperationLogger.logCancelled(
                    logItemId = logItemId,
                    operationName,
                    firstItem,
                    secondItem,
                    e
                )
            },
            onError = { t ->
                fileOperationLogger.logError(
                    logItemId = logItemId,
                    operationName,
                    firstItem,
                    secondItem,
                    t
                )
            },
            finally = {
                operationJobsHolder.removeJob(logItemId)
                ProgressHolder.removeProgressState(logItemId)
            },
            block = {
                codeBlock.invoke()
            }
        )
    }

    private val fileOperationLogger by lazy {
        fileOperationLoggerFactory.create(taskId, executionId)
    }
}


@AssistedFactory
interface BasicFileInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): BasicFileInstructionsProcessor
}