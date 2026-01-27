package com.github.aakumykov.sync_dir_to_cloud.utils

import androidx.core.util.Supplier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException

class CritNonCritExecutor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun execute(
        scope: CoroutineScope,
        isCriticalSupplier: Supplier<Boolean>,
        onCriticalError: (t: Throwable) -> Unit,
        onNonCriticalError: (t: Throwable) -> Unit,
        onCancelled: (e: CancellationException) -> Unit = {},
        codeBlock:  suspend () -> Unit
    ) {
        val workWithCancellationCatching = suspend {
            try {
                codeBlock.invoke()
            } catch (e: CancellationException) {
                onCancelled.invoke(e)
            }
        }

        val nonCriticalEH = CoroutineExceptionHandler { context, throwable ->
            onNonCriticalError.invoke(throwable)
        }

        val criticalEH = CoroutineExceptionHandler { context, throwable ->
            onCriticalError.invoke(throwable)
        }

        val exceptionHandler = if (isCriticalSupplier.get()) criticalEH
        else nonCriticalEH

        scope.launch {
            supervisorScope {
                launch (dispatcher + exceptionHandler) {
                    workWithCancellationCatching.invoke()
                }
            }
        }.join()
    }
}