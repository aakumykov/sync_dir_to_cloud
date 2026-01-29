package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope

interface InstructionsProcessor {
    suspend fun process(
        scope: CoroutineScope,
        @StringRes operationNameId: Int,
        processedObjectRelativePath: String,
        codeBlock: suspend () -> Unit
    )
}