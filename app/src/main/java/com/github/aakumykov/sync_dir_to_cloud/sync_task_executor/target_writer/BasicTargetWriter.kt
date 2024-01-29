package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.extensions.stripMultiSlash
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import java.io.File

abstract class BasicTargetWriter constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val taskId: String,
    private val sourceDirPath: String,
    private val targetDirPath: String
) : TargetWriter
{
    private var stopFlag: Boolean = false


    @Throws(IllegalStateException::class)
    override suspend fun writeToTarget(overwriteIfExists: Boolean) {

        stopFlag = false

        run dirsLoop@ {
            syncObjectReader.getSyncObjectsForTask(taskId)
                .filter { it.isDir }
                .forEach { syncObject ->

                    if (stopFlag) return@dirsLoop

                    writeSyncObjectToTarget(syncObject) {

                        val parentDirName = targetDirPath
                        val childDirName = (syncObject.relativeParentDirPath + syncObject.name).stripMultiSlash()

                        try {
                            cloudWriter()?.createDir(
                                parentDirName = parentDirName,
                                childDirName = childDirName
                            )
                        } catch (e: CloudWriter.AlreadyExistsException) {
                            MyLogger.d(tag(), "Каталог '$childDirName' уже существует в '$parentDirName'.")
                        }
                    }
                }
        }

        run filesLoop@ {
            syncObjectReader.getSyncObjectsForTask(taskId).filter { !it.isDir }
                .forEach { syncObject ->

                    if (stopFlag) return@filesLoop

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
    }

    override fun stopWriting() {
        stopFlag = true
    }


    protected abstract fun cloudWriter(): CloudWriter?

    protected abstract fun tag(): String


    private suspend fun writeSyncObjectToTarget(syncObject: SyncObject, writeAction: Runnable) {
        try {
            syncObjectStateChanger.changeState(syncObject.id, SyncObject.State.RUNNING)
            writeAction.run()
            syncObjectStateChanger.changeState(syncObject.id, SyncObject.State.SUCCESS)
        }
        catch (t: Throwable) {
            syncObjectStateChanger.setErrorState(syncObject.id, ExceptionUtils.getErrorMessage(t))
        }
    }
}
