package com.github.aakumykov.sync_dir_to_cloud.task_dirs_checker

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettings
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.OperationLogger
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
    private val operationLogger: OperationLogger,
    private val appSettings: AppSettings,
) {
    private val sourceReader: CloudReader by lazy { cloudReaderGetter.getSourceCloudReaderFor(syncTask) }
    private val targetReader: CloudReader by lazy { cloudReaderGetter.getTargetCloudReaderFor(syncTask) }

    private val sourceWriter: CloudWriter by lazy { cloudWriterGetter.getSourceCloudWriter(syncTask) }
    private val targetWriter: CloudWriter by lazy { cloudWriterGetter.getTargetCloudWriter(syncTask) }


    @Throws(SourceDirIsMissingException::class, TopDirIsMissingException::class)
    suspend fun checkTaskDirs() {
        checkAndFixSourceDir(syncTask)
        checkAndFixTargetDir(syncTask)
    }


    @Throws(SourceDirIsMissingException::class)
    private suspend fun checkAndFixSourceDir(syncTask: SyncTask) {

        val sourcePath = syncTask.sourcePath!!
        val sourceDirName = File(sourcePath).name.let { if ("" == it) sourcePath else it }
        val sourceBaseDirPath = File(sourcePath).parent ?: sourcePath

        val sourceDirExists = sourceReader.dirExists(sourcePath).getOrThrow()
        val sourceDirMustBeRestored = appSettings.restoreLostSourceAndTargetDirs

        // Если каталога нет и его не нужно пытаться восстановить, аварийно завершаю работу.
        if (!sourceDirExists && !sourceDirMustBeRestored)
            throw SourceDirIsMissingException(sourcePath)

        if (!sourceDirExists) {
            try {
                logExecutionStarted(R.string.re_creating_source_dir)
                sourceWriter.createDir(sourceBaseDirPath, sourceDirName)
                logExecutionFinished()
            } catch (e: Exception) {
                logExecutionError(e.errorMsg)
            }
        }
    }


    @Throws(TopDirIsMissingException::class)
    private suspend fun checkAndFixTargetDir(syncTask: SyncTask) {

        val targetPath = syncTask.targetPath!!
        val targetDirName = File(targetPath).name.let { if ("" == it) targetPath else it }
        val targetBaseDirPath = File(targetPath).parent ?: targetPath

        val targetDirExists = targetReader.dirExists(targetPath).getOrThrow()
        val targetDirMustBeRestored = appSettings.restoreLostSourceAndTargetDirs

        // Если каталога нет и его не нужно пытаться восстановить, аварийно завершаю работу.
        if (!targetDirExists && !targetDirMustBeRestored)
            throw TargetDirIsMissingException(targetPath)

        if (!targetDirExists) {
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
        operationLogger.log(TaskExecutionLogItem.createStartingItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = getString(messageId)
        ))
    }

    private suspend fun logExecutionError(errorMsg: String) {
        operationLogger.updateLog(TaskExecutionLogItem.createErrorItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = errorMsg,
            details = null,
        ))
    }

    private suspend fun logExecutionFinished() {
        operationLogger.updateLog(TaskExecutionLogItem.createFinishingItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = getString(R.string.EXECUTION_LOG_reading_source)
        ))
    }

    open class TopDirIsMissingException(message: String) : Exception(message)
    class SourceDirIsMissingException(absoluteDirPath: String) : TopDirIsMissingException(absoluteDirPath)
    class TargetDirIsMissingException(absoluteDirPath: String) : TopDirIsMissingException(absoluteDirPath)
}


@AssistedFactory
interface TaskDirsFixerAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TaskDirsFixer
}