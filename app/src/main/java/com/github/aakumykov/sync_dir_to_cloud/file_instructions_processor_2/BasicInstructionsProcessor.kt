package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class BasicInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val fileOperationLogger2Factory: FileOperationLogger2AssistedFactory,
    private val operationJobsHolder: OperationJobsHolder,
): InstructionsProcessor {

    override suspend fun process(
        parentScope: CoroutineScope,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?,
        codeBlock: suspend () -> Unit
    ) {
        val jobId = newRandomId

        runInCoroutineExtended(
            scope = parentScope,
            onStart = { job ->
                operationJobsHolder.addJob(jobId, job)
                fileOperationLogger2.logStarted(
                    jobId,
                    operationName,
                    firstItem,
                    secondItem)
            },
            onFinish = {
                fileOperationLogger2.logFinished(
                    operationName,
                    firstItem,
                    secondItem)
            },
            onCancel = { e ->
                fileOperationLogger2.logCancelled(
                    operationName,
                    firstItem,
                    secondItem,
                    e)
            },
            onError = { t ->
                fileOperationLogger2.logError(
                    operationName,
                    firstItem,
                    secondItem,
                    t)
            },
            finally = {
                operationJobsHolder.removeJob(jobId)
            }
        ) {
            codeBlock.invoke()
        }
    }

    private val fileOperationLogger2 by lazy {
        fileOperationLogger2Factory.create(taskId, executionId)
    }
}


@AssistedFactory
interface BasicInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): BasicInstructionsProcessor
}