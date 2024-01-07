package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.factory_and_creator.TargetWriterFactory3
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

// TODO: базовый класс
class YandexTargetWriter3 @AssistedInject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val cloudWriterCreator: CloudWriterCreator,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    @Assisted(AssistedArgName.AUTH_TOKEN)  private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID)  private val taskId: String,
    @Assisted(AssistedArgName.TARGET_DIR_PATH) private val targetDirPath: String
) : TargetWriter3 {

    private val yandexCloudWriter: CloudWriter? by lazy {
        cloudWriterCreator.createCloudWriter(StorageType.YANDEX_DISK, authToken)
    }

    @Throws(IllegalStateException::class)
    override suspend fun writeToTarget(overwriteIfExists: Boolean) {

        if (null == yandexCloudWriter)
            throw IllegalStateException("Cloud writer is null.")

        syncObjectReader.getSyncObjectsForTask(taskId).filter { it.isDir }
            .forEach { syncObject ->
                writeSyncObjectToTarget(syncObject) {
                    yandexCloudWriter?.createDir(targetDirPath, syncObject.name)
                }
            }

        syncObjectReader.getSyncObjectsForTask(taskId).filter { !it.isDir }
            .forEach { syncObject ->
                writeSyncObjectToTarget(syncObject) {
                    yandexCloudWriter?.putFile(
                        File(syncObject.sourcePath),
                        targetDirPath,
                        overwriteIfExists
                    )
                }
            }
    }

    // TODO: в базовый класс
    private suspend fun writeSyncObjectToTarget(syncObject: SyncObject, writeAction: Runnable) {
        try {
            syncObjectStateChanger.changeState(syncObject.id, SyncObject.State.RUNNING)
            writeAction.run()
            syncObjectStateChanger.changeState(syncObject.id, SyncObject.State.SUCCESS)
        }
        catch (t: Throwable) {
            syncObjectStateChanger.setErrorState(syncObject.id, ExceptionUtils.getErrorMessage(t))
            throw t
        }
    }

    @AssistedFactory
    interface Factory : TargetWriterFactory3 {
        override fun create(
            @Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
            @Assisted(AssistedArgName.TASK_ID) taskId: String,
            @Assisted(AssistedArgName.TARGET_DIR_PATH) targetDirPath: String
        ): YandexTargetWriter3
    }
}