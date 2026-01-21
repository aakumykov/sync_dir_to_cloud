package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.file_lister_navigator_selector.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger.ExecutionLogger2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger.ExecutionLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

// TODO: убрать Resources отсюда, перенести их в Logger.
class CoroutineSyncInstructionProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val executionLogger: ExecutionLogger,
    private val executionLogger2AssistedFactory: ExecutionLogger2AssistedFactory,
    private val resources: Resources,
) {
    private val executionLogger2: ExecutionLogger2 by lazy { executionLogger2AssistedFactory.create(taskId, executionId) }

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
                 executionLogger2.logExecutionStarted(logItemId, logMessage)

                 val nonCriticalExceptionHandler = CoroutineExceptionHandler { context, throwable ->
                     Log.w(TAG, "Некритичная ошибка: ${throwable.errorMsgExtended}")
                 }

                 if (isCritical) scope.launch {
                     executionBlock.invoke()
                 } else {
                     scope.launch {
                         supervisorScope {
                             launch (nonCriticalExceptionHandler) {
                                 executionBlock.invoke()
                             }
                         }
                     }
                 }

                 executionLogger.updateLog(TaskExecutionLogItem.createFinishingItem(taskId, executionId, text4log))
                 executionLogger2.logExecutionFinished(logItemId,logMessage)

             } catch (e: CancellationException) {
                 executionLogger.updateLog(TaskExecutionLogItem.createErrorItem(
                     taskId, executionId, text4log,"ОТМЕНЕНО"
                 ))
                 executionLogger2.logExecutionCancelled(logItemId,logMessage)
                 throw e
             }
             catch (throwable: Throwable) {
                 executionLogger.updateLog(TaskExecutionLogItem.createErrorItem(
                     taskId, executionId, text4log,throwable.errorMsg
                 ))
                 executionLogger2.logExecutionError(logItemId,logMessage, throwable)
                 if (isCritical)
                     throw throwable
             }
         }.join()
    }

    companion object {
        val TAG: String = CoroutineSyncInstructionProcessor::class.java.simpleName
    }
}


@AssistedFactory
interface CoroutineSyncInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID)  taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
        scope: CoroutineScope,
    ): CoroutineSyncInstructionProcessor
}