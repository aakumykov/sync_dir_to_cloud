package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.creator.SourceReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.creator.TargetWriterCreator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SyncTaskExecutor2 @Inject constructor(
    private val sourceReaderCreator: SourceReaderCreator,
    private val targetWriterCreator: TargetWriterCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val stateChanger: SyncTaskStateChanger
) {
    private var sourceReader: SourceReader? = null
    private var targetWriter: TargetWriter? = null

    suspend fun executeSyncTask(syncTask: SyncTask) {
        prepareReaderAndWriter(syncTask)

        val taskId = syncTask.id

        try {
            stateChanger.changeState(taskId, SyncTask.State.READING_SOURCE)
            sourceReader?.read(syncTask.sourcePath!!) // FIXME: sourcePath!!

            stateChanger.changeState(taskId, SyncTask.State.WRITING_TARGET)
            targetWriter?.write()

            stateChanger.changeState(taskId, SyncTask.State.SUCCESS)
        }
        catch (t: Throwable) {
            stateChanger.changeState(taskId, SyncTask.State.ERROR)
            Log.e(TAG, ExceptionUtils.getErrorMessage(t), t)
        }
    }


    private suspend fun prepareReaderAndWriter(syncTask: SyncTask) {

        val sourceAuthToken = "" // TODO: реализовать
        val targetAuthToken = cloudAuthReader.getCloudAuth(syncTask.id)?.authToken

        sourceReader = sourceReaderCreator.create(syncTask.sourceType, syncTask.id, sourceAuthToken)
        targetWriter = targetWriterCreator.create(syncTask.targetType, syncTask.id, targetAuthToken)
    }

    companion object {
        val TAG: String = SyncTaskExecutor2::class.java.simpleName
    }
}