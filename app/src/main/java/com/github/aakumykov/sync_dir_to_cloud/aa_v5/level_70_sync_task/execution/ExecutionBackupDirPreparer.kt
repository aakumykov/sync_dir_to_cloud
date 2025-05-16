package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.execution

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ExecutionBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val executionBackupDirCreatorAssistedFactory: ExecutionBackupDirCreatorAssistedFactory,
    private val syncTaskUpdater: SyncTaskUpdater,
) {
    //
    // ExecutionBackupDirCreator - класс, обладающий внутренним состоянием
    // (суффикс имени каталога), поэтому требует строго последовательного вызова,
    // что обеспечивается в методе [prepareExecutionBackupDirs], или создания
    // отдельного экземпляра для работу в Источнике или Приёмнике.
    //

    suspend fun prepareExecutionBackupDirs(syncSide: SyncSide) {
        return when (syncTask.syncMode!!) {
            SyncMode.SYNC -> prepareExecutionBackupDirInTarget()
            SyncMode.MIRROR -> {
                when(syncSide) {
                    SyncSide.SOURCE -> prepareExecutionBackupDirInSource()
                    SyncSide.TARGET -> prepareExecutionBackupDirInTarget()
                }
            }
        }
    }

    private suspend fun prepareExecutionBackupDirInSource() {
        createExecutionBackupDirInSource().also { dirName ->
            syncTaskUpdater.setSourceExecutionBackupDir(syncTask.id, dirName)
        }
    }


    private suspend fun prepareExecutionBackupDirInTarget() {
        createExecutionBackupDirInTarget().also { dirName ->
            syncTaskUpdater.setTargetExecutionBackupDir(syncTask.id, dirName)
        }
    }


    private suspend fun createExecutionBackupDirInSource(): String {
        return executionBackupDirCreator.createBaseBackupDirInSource()
    }


    private suspend fun createExecutionBackupDirInTarget(): String {
        return executionBackupDirCreator.createBaseBackupDirInTarget()
    }


    private val executionBackupDirCreator by lazy {
        executionBackupDirCreatorAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface ExecutionBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask): ExecutionBackupDirPreparer
}