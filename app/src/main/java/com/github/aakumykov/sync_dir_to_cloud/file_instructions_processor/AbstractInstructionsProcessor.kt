package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.launchWithStartCallback
import kotlinx.coroutines.CoroutineScope

abstract class AbstractInstructionsProcessor(
    private val fileOperationLogger2: FileOperationLogger2,
    private val operationJobsHolder: OperationJobsHolder,
) {
    fun process(scope: CoroutineScope, block: () -> Unit) {
        val jobId = newRandomId
        scope.launchWithStartCallback(
            onStart = { job ->
                operationJobsHolder.addJob(jobId, job)
            }
        ) {
            fileOperationLogger2.logStarted(jobId, startMessageId, description)
            block.invoke()
            fileOperationLogger2.logFinished(startMessageId, description)
        }
    }

    abstract val startMessageId: Int
    abstract val finishMessageId: Int
    abstract val description: String
}