package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.extensions.stripMultiSlash
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import java.io.File

abstract class BasicTargetWriter constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val taskId: String,
    private val sourceDirPath: String,
    private val targetDirPath: String
)
    : TargetWriter
{
    private suspend fun writeSyncObjectToTarget(syncObject: SyncObject, writeAction: Runnable) {
        try {
            syncObjectStateChanger.changeExecutionState(
                syncObject.id,
                ExecutionState.RUNNING,
                ""
            )
            writeAction.run()
            syncObjectStateChanger.changeExecutionState(
                syncObject.id,
                ExecutionState.NEVER,
                ""
            )
            syncObjectStateChanger.setSyncDate(syncObject.id, currentTime())
        }
        catch (t: Throwable) {
            val errorMsg = ExceptionUtils.getErrorMessage(t)
            syncObjectStateChanger.changeExecutionState(
                syncObject.id,
                ExecutionState.ERROR,
                errorMsg
            )
            MyLogger.e(TAG, errorMsg, t)
        }
    }


    @Throws(IllegalStateException::class)
    override suspend fun writeToTarget(overwriteIfExists: Boolean) {

        // Каталоги
        syncObjectReader.getNewAndChangedSyncObjectsForTask(taskId).filter { it.isDir }
            .forEach { syncObject ->
                writeSyncObjectToTarget(syncObject) {

                    val parentDirName = targetDirPath
                    val childDirName = (syncObject.relativeParentDirPath + CloudWriter.DS + syncObject.name).stripMultiSlash()

                    try {
                        cloudWriter()?.createDir(
                            basePath = parentDirName,
                            dirName = childDirName
                        )
                    } catch (e: CloudWriter.AlreadyExistsException) {
                        MyLogger.d(tag(), "Каталог '$childDirName' уже существует в '$parentDirName'.")
                    }
                }
            }

        // Файлы
        syncObjectReader.getNewAndChangedSyncObjectsForTask(taskId).filter { !it.isDir }
            .forEach { syncObject ->
                writeSyncObjectToTarget(syncObject) {

                    val pathInSource = (sourceDirPath +
                            CloudWriter.DS +
                            syncObject.relativeParentDirPath +
                            CloudWriter.DS +
                            syncObject.name)
                        .stripMultiSlash()

                    val pathInTarget = (targetDirPath +
                            CloudWriter.DS +
                            syncObject.relativeParentDirPath +
                            CloudWriter.DS +
                            syncObject.name)
                        .stripMultiSlash()

                    cloudWriter()?.putFile(
                        file = File(pathInSource),
                        targetPath = pathInTarget,
                        overwriteIfExists = overwriteIfExists
                    )
                }
            }
    }

    protected abstract fun cloudWriter(): CloudWriter?

    protected abstract fun tag(): String

    companion object {
        val TAG: String = BasicTargetWriter::class.java.simpleName
    }
}
