package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_reader.FileMetadata
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * После выполнения операции копирования необходимо добавить в БД
 * запись о новом объекте в хринилище. Эта запись должна содержать
 * аткуальные метаданные, иначе объект будет ошибочно распознан как
 * изменившийся при следующем запуске синхронизации.
 * Делается актуализация путём считывания данных о файле (папке?)
 * из хранилища.
 */
class SyncObjectActualizer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val cloudReaderGetter: CloudReaderGetter,
    private val syncObjectAdder: SyncObjectAdder
) {
    suspend fun addActualInfoAboutObjectTo(
        syncSide: SyncSide,
        correspondingObject: SyncObject,
    ) {
        cloudReader(syncSide)
            .getFileMetadata(absolutePathFor(correspondingObject, syncSide))
            .getOrThrow()
            .also { metadata ->
                syncObjectAdder.addSyncObject(successfullySyncedNewObject(
                    fileMetadata = metadata,
                    taskId = syncTask.id,
                    executionId = executionId,
                    syncSide = syncSide,
                ))
            }
    }

    private fun absolutePathFor(syncObject: SyncObject, syncSide: SyncSide): String {
        return syncObject.absolutePathIn(when(syncSide) {
            SyncSide.SOURCE -> syncTask.sourcePath!!
            SyncSide.TARGET -> syncTask.targetPath!!
        })
    }

    private suspend fun cloudReader(syncSide: SyncSide): CloudReader = when(syncSide) {
            SyncSide.SOURCE -> cloudReaderGetter.getSourceCloudReaderFor(syncTask)
            SyncSide.TARGET -> cloudReaderGetter.getTargetCloudReaderFor(syncTask)
    }

    private fun successfullySyncedNewObject(
        fileMetadata: FileMetadata,
        taskId: String,
        executionId: String,
        syncSide: SyncSide,
    ): SyncObject = SyncObject(
        id = randomUUID,
        taskId = taskId,
        executionId = executionId,

        syncSide = syncSide,
        relativeParentDirPath = "",
        isExistsInTarget = true,
        syncDate = currentTime(),
        syncError = "",
        name = fileMetadata.name,
        mTime = fileMetadata.modified,
        size = fileMetadata.size,
        isDir = fileMetadata.isDir,

        stateInStorage = StateInStorage.NEW,
        syncState = ExecutionState.SUCCESS,

        backupState = ExecutionState.NEVER,
        targetReadingState = ExecutionState.NEVER,
        deletionState = ExecutionState.NEVER,
        restorationState = ExecutionState.NEVER,
    )
}


@AssistedFactory
interface SyncObjectActualizerAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectActualizer
}
