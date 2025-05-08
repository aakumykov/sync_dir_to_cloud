package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.execution

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ExecutionBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val executionBackupDirCreatorAssistedFactory: ExecutionBackupDirCreatorAssistedFactory
) {
    //
    // ExecutionBackupDirCreator - класс, обладающий внутренним состоянием
    // (суффикс имени каталога), поэтому требует строго последовательного вызова,
    // что обеспечивается в методе [prepareExecutionBackupDirs], или создания
    // отдельного экземпляра для работу в Источнике или Приёмнике.
    //

    suspend fun prepareExecutionBackupDirs(): String {
        return when (syncTask.syncMode!!) {
            SyncMode.SYNC -> createExecutionBackupDirInTarget()
            SyncMode.MIRROR -> {
                createExecutionBackupDirInSource()
                createExecutionBackupDirInTarget()
            }
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