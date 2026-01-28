package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectCopier
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class CopyInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectCopierFactory: SyncObjectFileCopierAssistedFactory,
    private val fileOperationLogger2Factory: FileOperationLogger2AssistedFactory,
    private val operationJobsHolder: OperationJobsHolder,
)
    : AbstractInstructionsProcessor(
        taskId,
        executionId,
        fileOperationLogger2Factory,
        operationJobsHolder
    )
{
    suspend fun process(instruction: SyncInstruction) {

    }

    override val operationStartsMessageId: Int
        get() = TODO("Not yet implemented")

    override val operationFinishesMessageId: Int
        get() = TODO("Not yet implemented")

    override val operationDescription: String
        get() = TODO("Not yet implemented")
}

@AssistedFactory
interface CopyInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): CopyInstructionsProcessor
}