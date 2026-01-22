package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger.InstructionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger.InstructionLoggerAssistedFactory
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
    private val instructionLoggerAssistedFactory: InstructionLoggerAssistedFactory,
    private val resources: Resources,
) {
    private val instructionLogger: InstructionLogger by lazy { instructionLoggerAssistedFactory.create(taskId, executionId) }

     suspend fun process(
         isCritical: Boolean,
         logMessage: TextMessage,
         instructionBlock: suspend () -> Unit,
     ) {
         scope.launch {
             val text4log = logMessage.get(resources)
             val logItemId = newRandomId

             try {
                 executionLogger.log(TaskExecutionLogItem.createStartingItem(taskId, executionId, text4log))
                 instructionLogger.logInstructionExecutionStarted(logItemId, logMessage)

                 val nonCriticalExceptionHandler = CoroutineExceptionHandler { context, throwable ->
                     Log.w(TAG, "Некритичная ошибка: ${throwable.errorMsgExtended}")
                 }

                 if (isCritical) scope.launch {
                     instructionBlock.invoke()
                 } else {
                     scope.launch {
                         supervisorScope {
                             launch (nonCriticalExceptionHandler) {
                                 instructionBlock.invoke()
                             }
                         }
                     }
                 }

                 executionLogger.updateLog(TaskExecutionLogItem.createFinishingItem(taskId, executionId, text4log))
                 instructionLogger.logInstructionExecutionFinished(logItemId,logMessage)

             } catch (e: CancellationException) {
                 executionLogger.updateLog(TaskExecutionLogItem.createErrorItem(
                     taskId, executionId, text4log,"ОТМЕНЕНО"
                 ))
                 instructionLogger.logInstructionExecutionCancelled(logItemId,logMessage)
                 // FIXME: Нужно ли перевыбрасывать это исключение? Кому оно нужно?
                 throw e
             }
             catch (throwable: Throwable) {
                 executionLogger.updateLog(TaskExecutionLogItem.createErrorItem(
                     taskId, executionId, text4log,throwable.errorMsg
                 ))
                 instructionLogger.logInstructionExecutionError(logItemId,logMessage, throwable)
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