package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base

import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface InstructionsProcessor {
    suspend fun process(
        scope: CoroutineScope,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?,
        codeBlock: suspend () -> Unit
    ): Job
}