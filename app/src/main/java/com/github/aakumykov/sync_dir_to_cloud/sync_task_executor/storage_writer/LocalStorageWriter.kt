package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudWritersHolder
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.factory_and_creator.StorageWriterFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalStorageWriter @AssistedInject constructor(
    @Assisted(AssistedArgName.SOURCE_DIR_PATH) private val sourceDirPath: String,
    @Assisted(AssistedArgName.TARGET_DIR_PATH) private val targetDirPath: String,
    @Assisted(AssistedArgName.TASK_ID) private val taskId: String,
    @Assisted(AssistedArgName.AUTH_TOKEN) private val dummyAuthToken: String,
    syncObjectReader: SyncObjectReader,
    syncObjectStateChanger: SyncObjectStateChanger,
    private val cloudWritersHolder: CloudWritersHolder,
)
    : BasicStorageWriter(
        taskId = taskId,
        sourceDirPath = sourceDirPath,
        targetDirPath = targetDirPath,
        syncObjectStateChanger = syncObjectStateChanger,
        syncObjectReader = syncObjectReader,
    )
{
    private val localCloudWriter: CloudWriter?  by lazy {
        cloudWritersHolder.getCloudWriter(StorageType.LOCAL, taskId)
    }

    override val cloudWriter get() = localCloudWriter
    override val tag: String get() = LocalStorageWriter::class.java.simpleName


    @AssistedFactory
    interface Factory : StorageWriterFactory {
        override fun create(
            @Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
            @Assisted(AssistedArgName.TASK_ID) taskId: String,
            @Assisted(AssistedArgName.SOURCE_DIR_PATH) sourceDirPath: String,
            @Assisted(AssistedArgName.TARGET_DIR_PATH) targetDirPath: String,
        ): LocalStorageWriter
    }
}