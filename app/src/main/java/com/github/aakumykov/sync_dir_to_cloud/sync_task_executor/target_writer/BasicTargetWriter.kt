package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState
import com.github.aakumykov.sync_dir_to_cloud.extensions.stripMultiSlash
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import java.io.File

abstract class BasicTargetWriter (
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val taskId: String,
    private val sourceDirPath: String,
    private val targetDirPath: String
)
    : TargetWriter
{
    // TODO: SuspendRunnable --> kotlin.coroutines.Runnable
    @FunctionalInterface
    internal interface SuspendRunnable {
        suspend fun run()
    }

    override suspend fun writeToTarget(sourceFileStreamSupplier: SourceFileStreamSupplier,
                                       overwriteIfExists: Boolean) {

        // Удаляю в каталоге назначения объекты, удалённые в источнике.
        deleteDeletedFiles()
        deleteDeletedDirs()

        // Записываю (создаю) каталоги.
        syncObjectReader.getObjectsNeedsToBeSynched(taskId)
            .filter { it.isDir }
            .forEach { syncObject ->
                writeSyncObjectToTarget(syncObject, object: SuspendRunnable {
                    override suspend fun run() {

                        val parentDirName = targetDirPath
                        val childDirName = (syncObject.relativeParentDirPath + CloudWriter.DS + syncObject.name).stripMultiSlash()

                        try {
                            cloudWriter?.createDir(
                                basePath = parentDirName,
                                dirName = childDirName
                            )

                        } catch (e: CloudWriter.AlreadyExistsException) {
                            MyLogger.d(tag, "Каталог '$childDirName' уже существует в '$parentDirName'.")
                        }
                    }
                })
            }

        // Копирую файлы из источника в приёмник.
        syncObjectReader.getObjectsNeedsToBeSynched(taskId)
            .filter { !it.isDir }
            .forEach { syncObject ->

                writeSyncObjectToTarget(syncObject, object: SuspendRunnable {
                    override suspend fun run() {

                        val pathInSource = (sourceDirPath +
                                CloudWriter.DS +
                                syncObject.relativeParentDirPath +
                                CloudWriter.DS +
                                syncObject.name)
                            .stripMultiSlash()

                        sourceFileStreamSupplier
                            .getSourceFileStream(pathInSource)
                            .getOrNull()?.also {  inputStream ->

                                inputStream.use {

                                    val pathInTarget = (targetDirPath +
                                            CloudWriter.DS +
                                            syncObject.relativeParentDirPath +
                                            CloudWriter.DS +
                                            syncObject.name)
                                        .stripMultiSlash()

                                    cloudWriter?.putFile(
                                        inputStream,
                                        pathInTarget,
                                        overwriteIfExists
                                    )
                                }
                        }
                    }
                })
            }
    }


    private suspend fun writeSyncObjectToTarget(syncObject: SyncObject, writeAction: SuspendRunnable) {
        kotlinx.coroutines.Runnable {  }
        try {
            syncObjectStateChanger.changeExecutionState(syncObject.id, SyncState.RUNNING, "")
            writeAction.run()
            syncObjectStateChanger.changeExecutionState(syncObject.id, SyncState.SUCCESS, "")
            syncObjectStateChanger.setSyncDate(syncObject.id, currentTime())
        }
        catch (t: Throwable) {
            val errorMsg = ExceptionUtils.getErrorMessage(t)
            syncObjectStateChanger.changeExecutionState(syncObject.id, SyncState.ERROR, errorMsg)
            MyLogger.e(tag, errorMsg, t)
        }
    }


    private suspend fun deleteDeletedFiles() {
        syncObjectReader.getObjectsForTask(taskId, ModificationState.DELETED)
            .filter { !it.isDir }
            .forEach { syncObject -> deleteObjectInTarget(syncObject) }
    }


    private suspend fun deleteDeletedDirs() {
        syncObjectReader.getObjectsForTask(taskId, ModificationState.DELETED)
            .filter { it.isDir }
            .forEach { syncObject -> deleteObjectInTarget(syncObject) }
    }


    private suspend fun deleteObjectInTarget(syncObject: SyncObject) {
        writeSyncObjectToTarget(syncObject, object: SuspendRunnable {
            override suspend fun run() {
                val basePath = CloudWriter.composeFullPath(targetDirPath, syncObject.relativeParentDirPath)
                cloudWriter?.deleteFile(basePath, syncObject.name)
            }
        })
    }


    protected abstract val cloudWriter: CloudWriter?


    protected abstract val tag: String


    companion object {
        val TAG: String = BasicTargetWriter::class.java.simpleName
    }
}
