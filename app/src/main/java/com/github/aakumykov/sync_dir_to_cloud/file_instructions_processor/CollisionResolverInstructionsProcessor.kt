package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CollisionResolverInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,

    ) {
    fun process(instruction: Iterable<FileInstruction>) {

    }
}

@AssistedFactory
interface CollisionResolverInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): CollisionResolverInstructionsProcessor
}