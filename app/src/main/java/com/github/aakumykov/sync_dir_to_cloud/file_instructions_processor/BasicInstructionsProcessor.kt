package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import kotlinx.coroutines.CoroutineScope

class BasicInstructionsProcessor(
    private val taskId: String,
    private val executionId: String,
    private val fileOperationLogger2Factory: FileOperationLogger2AssistedFactory,
    private val operationJobsHolder: OperationJobsHolder,
): InstructionsProcessor {

    override suspend fun process(
        scope: CoroutineScope,
        @StringRes operationNameId: Int,
        relativeFilePath: String,
        codeBlock: suspend () -> Unit
    ) {
        val jobId = newRandomId

        runInCoroutineExtended(
            scope = scope,
            onStart = { job ->
                operationJobsHolder.addJob(jobId, job)
                fileOperationLogger2.logStarted(
                    jobId,
                    logMessageSupplier.operationMessageIdStarted,
                    logMessageSupplier.operationDescriptionStarted)
            },
            onFinish = {
                fileOperationLogger2.logFinished(
                    logMessageSupplier.operationMessageIdFinished,
                    logMessageSupplier.operationDescriptionFinished)
            },
            onCancel = { e ->
                operationJobsHolder.removeJob(jobId)
                fileOperationLogger2.logCancelled(
                    logMessageSupplier.operationMessageIdCancelled,
                    logMessageSupplier.operationDescriptionCancel,
                    e)
            },
            onError = { t ->
                fileOperationLogger2.logError(
                    logMessageSupplier.operationMessageIdError,
                    logMessageSupplier.operationDescriptionError,
                    t)
            },
        ) {
            codeBlock.invoke()
        }
    }

    private val fileOperationLogger2 by lazy {
        fileOperationLogger2Factory.create(taskId, executionId)
    }
}