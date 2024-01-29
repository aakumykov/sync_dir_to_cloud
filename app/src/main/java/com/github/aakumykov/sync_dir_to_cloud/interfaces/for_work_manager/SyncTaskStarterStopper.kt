package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager

import androidx.work.Operation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStarterStopper {
    // TODO: как сообщать об ошибке? Исключения, наверное...

    suspend fun startSyncTask(syncTask: SyncTask): Operation.State.SUCCESS

    suspend fun stopSyncTask(syncTask: SyncTask): Operation.State.SUCCESS
}
