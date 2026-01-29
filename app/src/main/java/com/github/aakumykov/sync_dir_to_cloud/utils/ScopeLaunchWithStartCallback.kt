package com.github.aakumykov.sync_dir_to_cloud.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchWithStartCallback(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onStart: suspend (job: Job) -> Unit,
    block: suspend () -> Unit,
): Job = launch(
    context = context,
    start = start,
) {
    block.invoke()
}.apply {
    // Спорное решение, так как вызов onStart получается асинхронным.
    launch {
        onStart.invoke(this@apply)
    }
}