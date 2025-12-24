package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import android.content.res.Resources
import com.github.aakumykov.file_lister_navigator_selector.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.LogItem2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger.ExecutionLogger2
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class CoroutineSyncInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val executionLogger: ExecutionLogger,
    private val executionLogger2: ExecutionLogger2,
    private val resources: Resources,
) {
     suspend fun process(
         isCritical: Boolean,
         logMessage: TextMessage,
         executionBlock: suspend () -> Unit,
     ) {
         scope.launch {
             val text4log = logMessage.get(resources)
             val logItemId = newRandomId

             try {
                 executionLogger.log(TaskExecutionLogItem.createStartingItem(taskId, executionId, text4log))
                 executionLogger2.logExecutionStarted(LogItem2.create(logItemId, logMessage.get(resources)))

                 scope.launch (jobForTask(scope, isCritical)) {
                     executionBlock.invoke()
                 }.join()

                 executionLogger.updateLog(TaskExecutionLogItem.createFinishingItem(taskId, executionId, text4log))
                 executionLogger2.logExecutionFinished(LogItem2.create(logItemId, logMessage.get(resources)))

             } catch (e: CancellationException) {
                 executionLogger.updateLog(TaskExecutionLogItem.createErrorItem(
                     taskId, executionId, text4log,"ОТМЕНЕНО"
                 ))
                 executionLogger2.logExecutionCancelled(
                     LogItem2.create(logItemId, e.errorMsg),
                     e
                 )
             }
             catch (throwable: Throwable) {
                 executionLogger.updateLog(TaskExecutionLogItem.createErrorItem(
                     taskId, executionId, text4log,throwable.errorMsg
                 ))
                 executionLogger2.logExecutionError(
                     LogItem2.create(logItemId,throwable.errorMsg),
                     throwable
                 )
             }
         }.join()
    }

    private fun jobForTask(scope: CoroutineScope, isCritical: Boolean): Job {
        val parentJob = scope.coroutineContext.job
        return if (isCritical) Job(parentJob) else SupervisorJob(parentJob)
    }
}


@AssistedFactory
interface CoroutineSyncInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID)  taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
        scope: CoroutineScope
    ): CoroutineSyncInstructionsProcessor
}