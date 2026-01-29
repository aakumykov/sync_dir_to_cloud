package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import kotlinx.coroutines.CoroutineScope

interface InstructionsProcessor {
    suspend fun process(
        scope: CoroutineScope,
        logMessageSupplier: FileOperationLogMessageSupplier,
        codeBlock: suspend () -> Unit
    )
}