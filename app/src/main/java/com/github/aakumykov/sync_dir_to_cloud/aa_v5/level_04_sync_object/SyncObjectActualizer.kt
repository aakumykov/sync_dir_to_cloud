package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_reader.FileMetadata
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.jvm.Throws

/**
 * После выполнения операции копирования необходимо добавить в БД
 * запись о новом объекте в хринилище. Эта запись должна содержать
 * аткуальные метаданные, иначе объект будет ошибочно распознан как
 * изменившийся при следующем запуске синхронизации.
 * Делается актуализация путём считывания данных о файле (папке?)
 * из хранилища.
 *
 * // FIXME: неудачное название. Действительная функция - добавление объекта.
 */
class SyncObjectActualizer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncObjectReader: SyncObjectReader,
    private val cloudReaderGetter: CloudReaderGetter,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater,
    private val syncObjectStateChanger: SyncObjectStateChanger,
) {
    suspend fun actualizeInfoAboutObject(
        correspondingObject: SyncObject,
        syncSide: SyncSide,
        syncState: ExecutionState,
    ) {
        // FIXME: сложная спорная логика
        syncObjectReader.getSyncObject(
            taskId = correspondingObject.taskId,
            syncSide = syncSide,
            name = correspondingObject.name,
            relativeParentDirPath = correspondingObject.relativeParentDirPath,
        )?.also { foundSyncObject ->
            if (foundSyncObject.isDeleted) {
                // Новый объект вместо удалённого.
                addNewSuccesscullySyncedSyncObject(syncSide, correspondingObject)
            } else {
                // Обновление существующего объекта.
                updateSyncObjectMetadata(foundSyncObject)
                setSyncState(foundSyncObject.id, syncState)
            }
        } ?: run {
            // Новый объект на пустое место.
            addNewSuccesscullySyncedSyncObject(syncSide, correspondingObject)
        }
    }

    private suspend fun setSyncState(objectId: String, syncState: ExecutionState) {
        syncObjectStateChanger.changeSyncState(objectId, syncState)
    }

    private suspend fun updateSyncObjectMetadata(foundSyncObject: SyncObject) {
        getFileMetadata(foundSyncObject.syncSide, absolutePathFor(foundSyncObject, foundSyncObject.syncSide))
            .also { fileMetadata: FileMetadata ->
                syncObjectUpdater.updateMetadata(
                    objectId = foundSyncObject.id,
                    size = fileMetadata.size,
                    mTime = fileMetadata.modified
                )
            }
    }

    private suspend fun addNewSuccesscullySyncedSyncObject(
        syncSide: SyncSide,
        correspondingObject: SyncObject,
    ) {
        getFileMetadata(syncSide, absolutePathFor(correspondingObject, syncSide))
            .also { metadata ->
                syncObjectAdder.addSyncObject(newSuccessfullySyncedObject(
                    fileMetadata = metadata,
                    taskId = syncTask.id,
                    executionId = executionId,
                    syncSide = syncSide,
                    relativeParentDirPath = correspondingObject.relativeParentDirPath,
                ))
            }
    }

    @Throws(NullPointerException::class)
    private suspend fun getFileMetadata(syncSide: SyncSide, absolutePath: String): FileMetadata {
        return cloudReader(syncSide)
            .getFileMetadata(absolutePath)
            .getOrThrow()!!
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

    private fun newSuccessfullySyncedObject(
        fileMetadata: FileMetadata,
        taskId: String,
        executionId: String,
        syncSide: SyncSide,
        relativeParentDirPath: String,
    ): SyncObject = SyncObject(
        id = randomUUID,
        taskId = taskId,
        executionId = executionId,

        syncSide = syncSide,
        relativeParentDirPath = relativeParentDirPath,
        isExistsInTarget = true,
        syncDate = currentTime(),
        syncError = "",
        name = fileMetadata.name,
        mTime = fileMetadata.modified,
        size = fileMetadata.size,
        isDir = fileMetadata.isDir,

        stateInStorage = StateInStorage.NEW,
        syncState = ExecutionState.SUCCESS,
        justChecked = true,

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
