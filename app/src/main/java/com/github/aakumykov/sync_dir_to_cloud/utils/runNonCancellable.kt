package com.github.aakumykov.sync_dir_to_cloud.utils

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

suspend fun runNonCancellable(block: suspend () -> Unit) {
    withContext(NonCancellable) {
        block.invoke()
    }
}