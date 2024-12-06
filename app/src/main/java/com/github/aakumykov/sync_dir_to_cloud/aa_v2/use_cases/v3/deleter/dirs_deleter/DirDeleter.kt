package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TARGET_DIR
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DirDeleter @AssistedInject constructor(
    @Assisted private val cloudWriter: CloudWriter,
    @Assisted(QUALIFIER_TARGET_DIR) private val targetDir: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
    private val syncObjectLogger: SyncObjectLogger,
){
    // TODO: сделать метод, удаляющий единичный каталог?
    suspend fun deleteDir(syncObject: SyncObject): Result<SyncObject> {
        return try {
            cloudWriter.deleteDirRecursively(targetDir, syncObject.name)
            syncObjectLogger.log(SyncObjectLogItem.createSuccess(
                taskId = syncObject.taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = getString(R.string.SYNC_OBJECT_LOGGER_deleting_dir)
            ))
            Result.success(syncObject)

        } catch (e: Exception) {
            ExceptionUtils.getErrorMessage(e).let { errorMsg ->
                syncObjectLogger.log(SyncObjectLogItem.createFailed(
                    taskId = syncObject.taskId,
                    executionId = executionId,
                    syncObject = syncObject,
                    operationName = getString(R.string.SYNC_OBJECT_LOGGER_deleting_dir),
                    errorMessage = errorMsg
                ))
                Result.failure(e)
            }
        }
    }

    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any) = resources.getString(stringRes, arguments)
}


@AssistedFactory
interface DirDeleterAssistedFactory {
    fun create(
        cloudWriter: CloudWriter,
        @Assisted(QUALIFIER_TARGET_DIR) targetDir: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): DirDeleter
}