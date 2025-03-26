package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger

import android.content.res.Resources
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncOperationLoggerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncOperationLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val syncOperationLoggerRepository: SyncOperationLoggerRepository,
    private val resources: Resources,
) {
    suspend fun logWaiting(syncInstruction6: SyncInstruction6) {

    }
}


@AssistedFactory
interface SyncOperationLoggerAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): SyncOperationLogger
}