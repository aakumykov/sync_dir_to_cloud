package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamWriterCancelledException
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.progressAsPartOf100
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogProgressUpdater
import com.github.aakumykov.sync_dir_to_cloud.progress_info_holder.ProgressInfoHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class SyncObjectFileCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncOptions: SyncOptions,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory5,
    private val streamToFileWriterAssistedFactory: StreamToFileWriterAssistedFactory,
    private val syncObjectLogProgressUpdater: SyncObjectLogProgressUpdater,
) {
    @Throws(StreamWriterCancelledException::class)
    suspend fun copyFileFromSourceToTarget(syncObject: SyncObject,
                                           absolutePathInTarget: String,
                                           overwriteIfExists: Boolean = syncOptions.overwriteIfExists) {
        fileWriter.putStreamToTarget(
            inputStreamGetter.getInputStreamInSource(syncObject),
            absolutePathInTarget,
            overwriteIfExists
        ) { transferredBytes ->
            // FIXME: внедрять!
            CoroutineScope(Dispatchers.IO).launch {
                syncObjectLogProgressUpdater.updateProgress(
                    objectId = syncObject.id,
                    taskId = syncTask.id,
                    executionId = executionId,
                    progressAsPartOf100 = syncObject.progressAsPartOf100(transferredBytes)
                )
            }
        }
    }

    @Throws(StreamWriterCancelledException::class)
    suspend fun copyFileFromTargetToSource(syncObject: SyncObject,
                                           absolutePathInSource: String,
                                           overwriteIfExists: Boolean = syncOptions.overwriteIfExists) {
        fileWriter.putStreamToSource(
            inputStreamGetter.getInputStreamInTarget(syncObject),
            absolutePathInSource,
            overwriteIfExists
        ) { transferredBytes ->
            // FIXME: внедрять!
            CoroutineScope(Dispatchers.IO).launch {
                syncObjectLogProgressUpdater.updateProgress(
                    objectId = syncObject.id,
                    taskId = syncTask.id,
                    executionId = executionId,
                    progressAsPartOf100 = syncObject.progressAsPartOf100(transferredBytes)
                )
            }
        }
    }


    private val inputStreamGetter: InputStreamGetter5
        get() = inputStreamGetterAssistedFactory.create(syncTask)

    private val fileWriter: StreamToFileWriter
        get() = streamToFileWriterAssistedFactory.create(syncTask)
}


@AssistedFactory
interface FileCopier5AssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectFileCopier
}
