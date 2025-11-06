package com.github.aakumykov.sync_dir_to_cloud.task_dirs_checker

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class TaskDirsFixer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val resources: Resources,
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
    private val executionLogger: ExecutionLogger,
) {
    private val sourceReader: CloudReader by lazy { cloudReaderGetter.getSourceCloudReaderFor(syncTask) }
    private val targetReader: CloudReader by lazy { cloudReaderGetter.getTargetCloudReaderFor(syncTask) }

    private val sourceWriter: CloudWriter by lazy { cloudWriterGetter.getSourceCloudWriter(syncTask) }
    private val targetWriter: CloudWriter by lazy { cloudWriterGetter.getTargetCloudWriter(syncTask) }

    suspend fun checkTaskDirs() {
        checkAndFixSourceDir(syncTask)
        checkAndFixTargetDir(syncTask)
    }

    private suspend fun checkAndFixSourceDir(syncTask: SyncTask) {
        val sourcePath = syncTask.sourcePath!!
        val sourceDirName = File(sourcePath).name.let { if ("" == it) sourcePath else it }
        val sourceBaseDirPath = File(sourcePath).parent ?: sourcePath

        if (!sourceReader.dirExists(sourcePath).getOrThrow()) {
            try {
                logExecutionStarted(R.string.re_creating_source_dir)
                sourceWriter.createDir(sourceBaseDirPath, sourceDirName)
                logExecutionFinished()
            } catch (e: Exception) {
                logExecutionError(e.errorMsg)
            }
        }
    }

    private suspend fun checkAndFixTargetDir(syncTask: SyncTask) {

        val targetPath = syncTask.targetPath!!
        val targetDirName = File(targetPath).name.let { if ("" == it) targetPath else it }
        val targetBaseDirPath = File(targetPath).parent ?: targetPath

        if (!targetReader.dirExists(targetPath).getOrThrow()) {
            try {
                logExecutionStarted(R.string.re_creating_target_dir)
                targetWriter.createDir(targetBaseDirPath, targetDirName)
                logExecutionFinished()
            } catch (e: Exception) {
                logExecutionError(e.errorMsg)
            }
        }
    }


    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)


    private suspend fun logExecutionStarted(@StringRes messageId: Int) {
        executionLogger.log(ExecutionLogItem.createStartingItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = getString(messageId)
        ))
    }

    private suspend fun logExecutionError(errorMsg: String) {
        executionLogger.updateLog(ExecutionLogItem.createErrorItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = errorMsg,
            details = null,
        ))
    }

    private suspend fun logExecutionFinished() {
        executionLogger.updateLog(ExecutionLogItem.createFinishingItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = getString(R.string.EXECUTION_LOG_reading_source)
        ))
    }
}


@AssistedFactory
interface TaskDirsFixerAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TaskDirsFixer
}