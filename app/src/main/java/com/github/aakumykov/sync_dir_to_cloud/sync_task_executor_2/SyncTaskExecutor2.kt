package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.creator.SourceReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.TargetWriter3
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.TargetWriterCreator3
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SyncTaskExecutor2 @Inject constructor(
    private val sourceReaderCreator: SourceReaderCreator,
    private val cloudWriterCreator: CloudWriterCreator,
    private val targetWriterCreator3: TargetWriterCreator3,
    private val cloudAuthReader: CloudAuthReader,
    private val stateChanger: SyncTaskStateChanger
) {
    private var sourceReader: SourceReader? = null
    private var cloudWriter: CloudWriter? = null
    private var targetWriter3: TargetWriter3? = null

    suspend fun executeSyncTask(syncTask: SyncTask) {

        prepareReaderAndWriter(syncTask)

        val taskId = syncTask.id

        try {
            stateChanger.changeState(taskId, SyncTask.State.READING_SOURCE)
            sourceReader?.read(syncTask.sourcePath!!) // FIXME: sourcePath!!

            stateChanger.changeState(taskId, SyncTask.State.WRITING_TARGET)
//            cloudWriter?

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
            ?: throw IllegalStateException("Target auth token cannot be null")

        val targetType: StorageType = syncTask.targetType
            ?: throw IllegalStateException("Target type cannot be null")

        sourceReader = sourceReaderCreator.create(syncTask.sourceType, syncTask.id, sourceAuthToken)
//        targetWriter = targetWriterCreator.create(syncTask.targetType, syncTask.id, targetAuthToken)
        targetWriter3 = targetWriterCreator3.create(targetType, targetAuthToken, syncTask.id)
    }

    companion object {
        val TAG: String = SyncTaskExecutor2::class.java.simpleName
    }
}