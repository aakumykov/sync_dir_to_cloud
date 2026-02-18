package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.progressAsPartOf100
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationLogProgressUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose.ProgressHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectFileCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncOptions: SyncOptions,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory5,
    private val streamToFileWriterAssistedFactory: StreamToFileWriterAssistedFactory,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val fileOperationLogProgressUpdater: FileOperationLogProgressUpdater
) {
    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    suspend fun copy(
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

            ProgressHolder.setProgress("", transferredBytes.toFloat())
        }

        syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
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
        }

        syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
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
    fun create(syncTask: SyncTask): SyncObjectFileCopier
}
