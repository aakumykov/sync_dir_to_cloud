package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.SyncObjectDirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

/**
 * Распределяет работу между FileCopier-ом и DirCreator-ом.
 */
class SyncObjectCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val databaseInteractingScope: CoroutineScope,
    private val syncObjectFileCopierAssistedFactory: SyncObjectFileCopierAssistedFactory,
    private val syncObjectDirCreatorAssistedFactory: DirCreator5AssistedFactory,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectActualizerAssistedFactory: SyncObjectActualizerAssistedFactory,
){
    // TODO: разобраться, как overwriteIfExists сочетается с бекапом

    private val fileCopier: SyncObjectFileCopier by lazy { syncObjectFileCopierAssistedFactory.create(syncTask, executionId, databaseInteractingScope) }

    private val dirCreator: SyncObjectDirCreator by lazy { syncObjectDirCreatorAssistedFactory.create(syncTask) }

    private val syncObjectActualizer: SyncObjectActualizer by lazy { syncObjectActualizerAssistedFactory.create(syncTask, executionId) }


    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    suspend fun copySyncObjectFromSourceToTarget(syncObject: SyncObject, overwriteIfExists: Boolean) {

        if (syncObject.isDir) createDirInTarget(syncObject)
        else copyFileFromSourceToTarget(syncObject, overwriteIfExists)

        syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)

        syncObjectActualizer.actualizeInfoAboutObject(
            syncObject,
            SyncSide.TARGET,
            ExecutionState.SUCCESS
        )
    }


    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    suspend fun copySyncObjectFromTargetToSource(syncObject: SyncObject, overwriteIfExists: Boolean) {
        if (syncObject.isDir) createDirInSource(syncObject)
        else copyFileFromTargetToSource(syncObject, overwriteIfExists)

        syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)

        syncObjectActualizer.actualizeInfoAboutObject(
            syncObject,
            SyncSide.SOURCE,
            ExecutionState.SUCCESS
        )
    }


    // FIXME: аргумент "overwriteIfExists" не используется
    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    private suspend fun copyFileFromSourceToTarget(syncObject: SyncObject, overwriteIfExists: Boolean) {
        fileCopier.copyFileFromSourceToTarget(
            syncObject,
            syncObject.absolutePathIn(syncTask.targetPath!!)
        )
    }


    // FIXME: аргумент "overwriteIfExists" не используется
    @Throws(StreamToFileWriter.StreamWriterCancelledException::class)
    private suspend fun copyFileFromTargetToSource(syncObject: SyncObject, overwriteIfExists: Boolean) {
        fileCopier.copyFileFromTargetToSource(
            syncObject,
            syncObject.absolutePathIn(syncTask.sourcePath!!)
        )
    }


    @Throws(Exception::class)
    private suspend fun createDirInTarget(syncObject: SyncObject) {
        dirCreator.createDirInTarget(
            basePath = syncObject.basePathIn(syncTask.targetPath!!),
            dirName = syncObject.name
        )
    }


    @Throws(Exception::class)
    private suspend fun createDirInSource(syncObject: SyncObject) {
        dirCreator.createDirInSource(
            basePath = syncObject.basePathIn(syncTask.sourcePath!!),
            dirName = syncObject.name
        )
    }
}


@AssistedFactory
interface ItemCopierAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String, databaseInteractingScope: CoroutineScope): SyncObjectCopier
}