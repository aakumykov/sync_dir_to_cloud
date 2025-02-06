package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import javax.inject.Inject

// FIXME: зачем здесь репозиторий? Какие выгоды он даёт, инкапсулируя DAO?
//  ОТВЕТ: если ПО будет портировано на десктоп, где нет WorkManager, пригодится для увода
//  работы в фоновый поток...
//  ПАРИРОВАНИЕ: в фон на десктопе можно увести весь [SyncTaskExecutor] и не париться.

class SyncInstructionRepository @Inject constructor(
    private val syncInstructionDAO: SyncInstructionDAO,
) {
    suspend fun add(syncInstruction: SyncInstruction) {
        syncInstructionDAO.addSyncInstruction(syncInstruction)
    }

    suspend fun deleteAllFor(taskId: String, executionId: String) {
        syncInstructionDAO.deleteAllFor(taskId, executionId)
    }

    suspend fun getFor(taskId: String,
                       executionId: String,
                       sourceObjectId: String,
                       targetObjectId: String,
    ): SyncInstruction? {
        return syncInstructionDAO.get(
            taskId = taskId,
            executionId = executionId,
            sourceObjectId = sourceObjectId,
            targetObjectId = targetObjectId
        )
    }
}