package com.github.aakumykov.sync_dir_to_cloud.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// Ценой пропуска исключений в дочерних корутинах сделал коллбек "onError" suspend.
// FIXME: тестировать!
fun runInCoroutineExtended(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    coroutineStart: CoroutineStart =  CoroutineStart.DEFAULT,
    onStart: suspend (job: Job) -> Unit,
    onFinish: suspend () -> Unit,
    onCancel: suspend (e: CancellationException) -> Unit,
    onError: suspend (throwable: Throwable) -> Unit,
    finally: suspend () -> Unit = {},
    block: suspend () -> Unit
): Job {
    return scope.launchWithStartCallback (
        context = context,
        start = coroutineStart,
        onStart = { job -> onStart.invoke(job) }
    ) {
        try {
            block.invoke()
            onFinish.invoke()
        } catch (e: CancellationException) {
            onCancel.invoke(e)
        } catch (t: Throwable) {
            onError.invoke(t)
        }
        finally {
            finally.invoke()
        }
    }
}