package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2

import com.github.aakumykov.kotlin_playground.target_writers.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.SourceReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.TargetWriterFactory
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class SyncTaskExecutor2 @Inject constructor(
    private val sourceReaderFactory: SourceReaderFactory,
    private val targetWriterFactory: TargetWriterFactory,
    private val cloudAuthReader: CloudAuthReader,
    private val syncTaskStateChanger: SyncTaskStateChanger
) {
    private var sourceReader: SourceReader? = null
    private var targetWriter: TargetWriter? = null

    suspend fun executeSyncTask(syncTask: SyncTask) {
        prepareReaderAndWriter(syncTask)
    }

    private suspend fun prepareReaderAndWriter(syncTask: SyncTask) {
        val cloudAuth = cloudAuthReader.getCloudAuth(syncTask.id)
//        sourceReader = sourceReaderFactory.create(StorageType.LOCAL, cloudAuth)
//        targetWriter = targetWriterFactory.create(syncTask.targetType, )
    }
}