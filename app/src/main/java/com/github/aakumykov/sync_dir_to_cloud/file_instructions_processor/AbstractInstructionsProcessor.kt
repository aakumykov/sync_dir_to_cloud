package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import kotlinx.coroutines.CoroutineScope

abstract class AbstractInstructionsProcessor(
    private val fileOperationLogger2: FileOperationLogger2,
    private val operationJobsHolder: OperationJobsHolder,
) {
    fun process(scope: CoroutineScope, block: () -> Unit) {
        val jobId = newRandomId

        runInCoroutineExtended(
            scope = scope,
            onStart = { job ->
                operationJobsHolder.addJob(jobId, job)
                fileOperationLogger2.logStarted(jobId, operationStartsMessageId, operationDescription)
            },
            onFinish = {
                fileOperationLogger2.logFinished(operationStartsMessageId, operationDescription)
            },
            onCancel = { e ->
                operationJobsHolder.removeJob(jobId)
            },
            onError = { t ->
                fileOperationLogger2.logError(operationStartsMessageId, operationDescription, t)
            },
        ) {
            block.invoke()
        }
    }

    abstract val operationStartsMessageId: Int
    abstract val operationFinishesMessageId: Int
    abstract val operationDescription: String
}