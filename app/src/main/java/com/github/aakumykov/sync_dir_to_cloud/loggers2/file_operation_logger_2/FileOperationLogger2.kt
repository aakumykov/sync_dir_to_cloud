package com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem2
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository2
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileOperationLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: FileOperationLogRepository2,
    private val resources: Resources,
) {
    suspend fun logStarted(
        jobId: String,
        @StringRes messageId: Int,
        filePath: String
    ) {
        runNonCancellable {
            repository.add(
                FileOperationLogItem2.create(
                    logItemType = LogItemType.BUSY,
                    taskId = taskId,
                    executionId = executionId,
                    message = resources.getString(messageId),
                    filePath = filePath,
                    jobId = jobId
                )
            )
        }
    }
}


@AssistedFactory
interface FileOperationLogger2AssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): FileOperationLogger2
}