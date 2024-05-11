package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.extensions.stripMultiSlash
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import java.io.InputStream

abstract class BasicTargetWriter (
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val taskId: String,
    private val sourceDirPath: String,
    private val targetDirPath: String
)
    : TargetWriter
{
    protected abstract val cloudWriter: CloudWriter?
    protected abstract val tag: String


    // TODO: SuspendRunnable --> kotlin.coroutines.Runnable
    @FunctionalInterface
    internal interface SuspendRunnable {
        suspend fun run()
    }


    override suspend fun writeToTarget(sourceFileStreamSupplier: SourceFileStreamSupplier,
                                       overwriteIfExists: Boolean) {
        deleteDeletedFiles()
        deleteDeletedDirs()
        copyDirs()
        copyFiles(sourceFileStreamSupplier, overwriteIfExists)
    }


    private suspend fun copyDirs() {

        syncObjectReader.getObjectsNeedsToBeSynched(taskId)
            .filter { it.isDir }
            .forEach { syncObject ->

                MyLogger.d(TAG, "Создание каталога (${classNameWithHash()}): '${syncObject.name}'")

                writeSyncObjectToTarget(syncObject, object: SuspendRunnable {
                    override suspend fun run() {

                        val parentDirName = targetDirPath
                        val childDirName = (syncObject.relativeParentDirPath + CloudWriter.DS + syncObject.name).stripMultiSlash()

                        try {
                            cloudWriter?.createDir(
                                basePath = parentDirName,
                                dirName = childDirName
                            )

                        } catch (throwable: Throwable) {
//                            MyLogger.d(tag, "Каталог '$childDirName' уже существует в '$parentDirName'.")
                            markObjectAsFailed(syncObject, throwable)
                        }
                    }
                })
            }
    }


    private suspend fun copyFiles(
        sourceFileStreamSupplier: SourceFileStreamSupplier,
        overwriteIfExists: Boolean
    ) {

        syncObjectReader.getObjectsNeedsToBeSynched(taskId)
            .filter { !it.isDir }
            .forEach { syncObject ->

                MyLogger.d(TAG, "Отправка файла (${classNameWithHash()}): '${syncObject.name}'")

                writeSyncObjectToTarget(syncObject, object: SuspendRunnable {
                    override suspend fun run() {

                        // TODO: вынести в отдельный метод
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

                        try {
                            sourceFileStreamSupplier
                                .getSourceFileStream(pathInSource)
                                .getOrThrow()
                                .also { inputStream ->
                                    writeFromInputStreamToTarget(
                                        inputStream,
                                        pathInTarget,
                                        overwriteIfExists
                                    )
                                }
                        } catch (throwable: Throwable) {
                            markObjectAsFailed(syncObject, throwable)
                        }
                    }
                })
            }
    }


    private suspend fun writeSyncObjectToTarget(syncObject: SyncObject, writeAction: SuspendRunnable) {
        kotlinx.coroutines.Runnable {  }
        try {
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.RUNNING, "")
            writeAction.run()
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.SUCCESS, "")
            syncObjectStateChanger.setSyncDate(syncObject.id, currentTime())
        }
        catch (t: Throwable) {
            val errorMsg = ExceptionUtils.getErrorMessage(t)
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.ERROR, errorMsg)
            MyLogger.e(tag, errorMsg, t)
        }
    }

    private fun writeFromInputStreamToTarget(inputStream: InputStream, pathInTarget: String, overwriteIfExists: Boolean) {
        inputStream.use {
            cloudWriter?.putFile(
                inputStream,
                pathInTarget,
                overwriteIfExists
            )
        }
    }


    private suspend fun markObjectAsFailed(syncObject: SyncObject, throwable: Throwable) {
        syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(throwable))
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


    companion object {
        val TAG: String = BasicTargetWriter::class.java.simpleName
    }
}
