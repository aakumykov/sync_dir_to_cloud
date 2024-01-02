package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class YandexTargetWriter3 @AssistedInject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val cloudWriterCreator: CloudWriterCreator,
    @Assisted(AssistedArgName.AUTH_TOKEN)  private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID)  private val taskId: String
) : TargetWriter3 {

    private val yandexCloudWriter: CloudWriter? by lazy {
        cloudWriterCreator.createCloudWriter(StorageType.YANDEX_DISK, authToken)
    }

    @Throws(IllegalStateException::class)
    override suspend fun writeToTarget() {

        if (null == yandexCloudWriter)
            throw IllegalStateException("Cloud writer is null.")

        syncObjectReader.getSyncObjectsForTask(taskId).filter {it.isDir }
            .forEach { syncObject ->
                yandexCloudWriter?.createDir(syncObject.path)
            }
    }

    @AssistedFactory
    interface Factory : TargetWriterFactory3 {
        override fun create(@Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
                            @Assisted(AssistedArgName.TASK_ID) taskId: String): YandexTargetWriter3
    }
}