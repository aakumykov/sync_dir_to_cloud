package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.factory_and_creator.TargetWriterFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class YandexTargetWriter @AssistedInject constructor(
    syncObjectReader: SyncObjectReader,
    syncObjectStateChanger: SyncObjectStateChanger,
    private val cloudWriterCreator: CloudWriterCreator,
    @Assisted(AssistedArgName.AUTH_TOKEN)  private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID)  private val taskId: String,
    @Assisted(AssistedArgName.SOURCE_DIR_PATH) private val sourceDirPath: String,
    @Assisted(AssistedArgName.TARGET_DIR_PATH) private val targetDirPath: String,
)
    : BasicTargetWriter(
        syncObjectReader = syncObjectReader,
        syncObjectStateChanger = syncObjectStateChanger,
        taskId = taskId,
        sourceDirPath = sourceDirPath,
        targetDirPath = targetDirPath
    )
{
    private val yandexCloudWriter: CloudWriter? by lazy {
        cloudWriterCreator.createCloudWriter(StorageType.YANDEX_DISK, authToken)
    }

    override val cloudWriter get() = yandexCloudWriter

    override val tag: String get() = YandexTargetWriter::class.java.simpleName

    @AssistedFactory
    interface Factory : TargetWriterFactory {
        override fun create(
            @Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
            @Assisted(AssistedArgName.TASK_ID) taskId: String,
            @Assisted(AssistedArgName.SOURCE_DIR_PATH) sourceDirPath: String,
            @Assisted(AssistedArgName.TARGET_DIR_PATH) targetDirPath: String,
        ): YandexTargetWriter
    }
}