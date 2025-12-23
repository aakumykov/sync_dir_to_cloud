package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.progressAsPartOf100
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogProgressUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SyncObjectFileCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val databaseInteractingScope: CoroutineScope,
    private val syncOptions: SyncOptions,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory5,
    private val streamToFileWriterAssistedFactory: StreamToFileWriterAssistedFactory,
    private val syncObjectLogProgressUpdater: SyncObjectLogProgressUpdater,
) {
    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    suspend fun copyFileFromSourceToTarget(
        syncObject: SyncObject,
        absolutePathInTarget: String,
        overwriteIfExists: Boolean = syncOptions.overwriteIfExists
    ) {
        streamToFileWriter.putStreamToTarget(
            inputStreamGetter.getInputStreamInSource(syncObject),
            absolutePathInTarget,
            overwriteIfExists
        ) { transferredBytes ->

            logProgress(syncObject.size, transferredBytes, syncObject.progressAsPartOf100(transferredBytes))

            databaseInteractingScope.launch {
                syncObjectLogProgressUpdater.updateProgress(
                    objectId = syncObject.id,
                    taskId = syncTask.id,
                    executionId = executionId,
                    progressAsPartOf100 = syncObject.progressAsPartOf100(transferredBytes)
                )
            }
        }
    }

    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    suspend fun copyFileFromTargetToSource(
        syncObject: SyncObject,
        absolutePathInSource: String,
        overwriteIfExists: Boolean = syncOptions.overwriteIfExists
    ) {
        streamToFileWriter.putStreamToSource(
            inputStreamGetter.getInputStreamInTarget(syncObject),
            absolutePathInSource,
            overwriteIfExists
        ) { transferredBytes ->

            logProgress(syncObject.size, transferredBytes, syncObject.progressAsPartOf100(transferredBytes))

            databaseInteractingScope.launch {
                syncObjectLogProgressUpdater.updateProgress(
                    objectId = syncObject.id,
                    taskId = syncTask.id,
                    executionId = executionId,
                    progressAsPartOf100 = syncObject.progressAsPartOf100(transferredBytes)
                )
            }
        }
    }

    private fun logProgress(fileSize: Long, transferredBytes: Long, percent: Int) {
//        Log.d(TAG, ("${transferredBytes}/${fileSize} (${percent}%)"))
    }


    private val inputStreamGetter: InputStreamGetter5
        get() = inputStreamGetterAssistedFactory.create(syncTask)

    private val streamToFileWriter: StreamToFileWriter
        get() = streamToFileWriterAssistedFactory.create(syncTask)


    companion object {
        val TAG: String = SyncObjectFileCopier::class.java.simpleName
    }
}


@AssistedFactory
interface SyncObjectFileCopierAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        databaseInteractingScope: CoroutineScope,
    ): SyncObjectFileCopier
}
