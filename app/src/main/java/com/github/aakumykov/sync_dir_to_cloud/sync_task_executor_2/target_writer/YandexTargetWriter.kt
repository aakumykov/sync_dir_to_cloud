package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer


import com.github.aakumykov.file_uploader.OkhttpFileUploader
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriterAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class YandexTargetWriter @AssistedInject constructor(
    @Assisted(AssistedArgName.AUTH_TOKEN) private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID) private val taskId: String,
    private val syncObjectReader: SyncObjectReader,
    private val fileUploader: OkhttpFileUploader
) : TargetWriter {

    override suspend fun write() {
        syncObjectReader.getSyncObjectsForTask(taskId).forEach { syncObject ->

        }
    }

    @AssistedFactory
    interface Factory : TargetWriterAssistedFactory {
        override fun create(
            @Assisted(AssistedArgName.AUTH_TOKEN)authToken: String,
            @Assisted(AssistedArgName.TASK_ID) taskId: String
        ): YandexTargetWriter
    }

    companion object {
        val TAG: String = YandexTargetWriter::class.java.simpleName
    }
}