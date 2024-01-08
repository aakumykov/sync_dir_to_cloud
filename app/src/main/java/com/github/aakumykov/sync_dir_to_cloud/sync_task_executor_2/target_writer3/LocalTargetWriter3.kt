package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.factory_and_creator.TargetWriterFactory3
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalTargetWriter3 @AssistedInject constructor(
    syncObjectReader: SyncObjectReader,
    syncObjectStateChanger: SyncObjectStateChanger,
    private val cloudWriterCreator: CloudWriterCreator,
    @Assisted(AssistedArgName.AUTH_TOKEN) private val authToken: String, // не используется
    @Assisted(AssistedArgName.TASK_ID) private val taskId: String,
    @Assisted(AssistedArgName.SOURCE_DIR_PATH) private val sourceDirPath: String,
    @Assisted(AssistedArgName.TARGET_DIR_PATH) private val targetDirPath: String,
)
    : BasicTargetWriter3(
        taskId = taskId,
        syncObjectStateChanger = syncObjectStateChanger,
        syncObjectReader = syncObjectReader,
        sourceDirPath = sourceDirPath,
        targetDirPath = targetDirPath,
    )
{
    private val localCloudWriter: CloudWriter?  by lazy {
        cloudWriterCreator.createCloudWriter(StorageType.LOCAL, taskId)
    }

    override fun cloudWriter(): CloudWriter? = localCloudWriter

    @AssistedFactory
    interface Factory : TargetWriterFactory3 {
        override fun create(
            @Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
            @Assisted(AssistedArgName.TASK_ID) taskId: String,
            @Assisted(AssistedArgName.SOURCE_DIR_PATH) sourceDirPath: String,
            @Assisted(AssistedArgName.TARGET_DIR_PATH) targetDirPath: String,
        ): LocalTargetWriter3
    }
}