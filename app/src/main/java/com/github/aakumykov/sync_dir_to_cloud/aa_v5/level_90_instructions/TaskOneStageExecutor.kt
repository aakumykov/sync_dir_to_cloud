package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.TaskOneStageExecutor.Companion.TAG
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger.InstructionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger.InstructionLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class TaskOneStageExecutor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val instructionLoggerAssistedFactory: InstructionLoggerAssistedFactory,
) {
    suspend fun process(
         isCritical: Boolean,
         logMessage: TextMessage,
         codeBlock: suspend () -> Unit,
     ) {
         scope.launch {
             try {
                 instructionLogger.logInstructionExecutionStarted(logMessage)

                 val nonCriticalExceptionHandler = CoroutineExceptionHandler { context, throwable ->
                     Log.w(TAG, "Некритичная ошибка: ${throwable.errorMsgExtended}")
                 }

                 if (isCritical) scope.launch {
                     codeBlock.invoke()
                 }.join()
                 else {
                     scope.launch {
                         supervisorScope {
                             launch (nonCriticalExceptionHandler) {
                                 codeBlock.invoke()
                             }.join()
                         }
                     }.join()
                 }

                 instructionLogger.logInstructionExecutionFinished(logMessage)

             } catch (e: CancellationException) {
                 instructionLogger.logInstructionExecutionCancelled(logMessage)
                 // FIXME: Нужно ли перевыбрасывать это исключение? Кому оно нужно?
                 throw e
             }
             catch (throwable: Throwable) {
                 instructionLogger.logInstructionExecutionError(logMessage, throwable)
                 if (isCritical)
                     throw throwable
             }
         }.join()
    }

    private val instructionLogger: InstructionLogger by lazy {
        instructionLoggerAssistedFactory.create(taskId, executionId) }

    companion object {
        val TAG: String = TaskOneStageExecutor::class.java.simpleName
    }
}


@AssistedFactory
interface TaskOneStageExecutorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID)  taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
        scope: CoroutineScope,
    ): TaskOneStageExecutor
}


abstract class CodeBlockExecutor(
    private val scope: CoroutineScope,
    private val onStart: () -> Unit,
    private val onFinish: () -> Unit,
    private val onCancelled: (e: CancellationException) -> Unit,
    private val onCriticalError: (t: Throwable) -> Unit,
    private val onNonCriticalError: (t: Throwable) -> Unit,
) {
    suspend fun process(
        isCritical: Boolean,
        logMessage: TextMessage,
        codeBlock: suspend () -> Unit,
    ) {
        scope.launch {
            try {
                onStart.invoke()

                val nonCriticalExceptionHandler = CoroutineExceptionHandler { context, throwable ->
                    onNonCriticalError.invoke(throwable)
                }

                if (isCritical) scope.launch {
                    codeBlock.invoke()
                }.join()
                else {
                    scope.launch {
                        supervisorScope {
                            launch (nonCriticalExceptionHandler) {
                                codeBlock.invoke()
                            }.join()
                        }
                    }.join()
                }

                onFinish.invoke()

            } catch (e: CancellationException) {
                onCancelled.invoke(e)
                throw e
            }
            catch (throwable: Throwable) {
                onCriticalError.invoke(throwable)
            }
        }.join()
    }
}


class TaskOneStageCodeBlockExecutor(

) {

}