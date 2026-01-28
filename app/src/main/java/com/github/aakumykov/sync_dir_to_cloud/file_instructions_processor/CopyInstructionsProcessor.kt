package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CopyInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private final val fileOperationLogger2: FileOperationLogger2,
    private final val operationJobsHolder: OperationJobsHolder
)
    : AbstractInstructionsProcessor(fileOperationLogger2, operationJobsHolder)
{
    fun process(instruction: SyncInstruction) {
        TODO("Not yet implemented")
    }

    override val startMessageId: Int
        get() = TODO("Not yet implemented")

    override val finishMessageId: Int
        get() = TODO("Not yet implemented")

    override val description: String
        get() = TODO("Not yet implemented")
}

@AssistedFactory
interface CopyInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): CopyInstructionsProcessor
}