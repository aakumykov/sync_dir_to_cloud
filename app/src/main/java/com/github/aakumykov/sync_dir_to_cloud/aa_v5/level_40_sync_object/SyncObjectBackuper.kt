package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper.FileAndDirBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectBackuper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectDBDeleter: SyncObjectDBDeleter,
    private val fileAndDirBackuperAssistedFactory: FileAndDirBackuperAssistedFactory,
) {
    suspend fun backupWithCopyInSource(syncInstruction: SyncInstruction) {

    }

    suspend fun backupWithCopyInTarget(syncInstruction: SyncInstruction) {

    }


    suspend fun backupWithMoveInSource(syncInstruction: SyncInstruction) {

    }

    suspend fun backupWithMoveInTarget(syncInstruction: SyncInstruction) {

    }



    private val sourceBackuper by lazy {
        fileAndDirBackuperAssistedFactory.create(syncTask, SyncSide.SOURCE)
    }

    private val targetBackuper by lazy {
        fileAndDirBackuperAssistedFactory.create(syncTask, SyncSide.TARGET)
    }
}


@AssistedFactory
interface SyncObjectBackuperAssistedFactory {
    fun create(syncTask: SyncTask): SyncObjectBackuper
}