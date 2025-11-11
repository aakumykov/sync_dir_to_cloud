package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamWriterCancelledException
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate.InputStreamGetterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectFileCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncOptions: SyncOptions,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory5,
    private val streamToFileWriterAssistedFactory: StreamToFileWriterAssistedFactory,
) {
    @Throws(StreamWriterCancelledException::class)
    suspend fun copyFileFromSourceToTarget(syncObject: SyncObject,
                                           absolutePathInTarget: String,
                                           overwriteIfExists: Boolean = syncOptions.overwriteIfExists) {
        fileWriter.putStreamToTarget(
            inputStreamGetter.getInputStreamInSource(syncObject),
            absolutePathInTarget,
            overwriteIfExists
        )
    }

    @Throws(StreamWriterCancelledException::class)
    suspend fun copyFileFromTargetToSource(syncObject: SyncObject,
                                           absolutePathInSource: String,
                                           overwriteIfExists: Boolean = syncOptions.overwriteIfExists) {
        fileWriter.putStreamToSource(
            inputStreamGetter.getInputStreamInTarget(syncObject),
            absolutePathInSource,
            overwriteIfExists
        )
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
