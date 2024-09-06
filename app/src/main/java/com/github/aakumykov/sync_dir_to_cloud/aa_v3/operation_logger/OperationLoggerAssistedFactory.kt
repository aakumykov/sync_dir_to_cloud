package com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLogger
import dagger.assisted.AssistedFactory

@AssistedFactory
interface OperationLoggerAssistedFactory {
    fun create(syncObjectLogger: SyncObjectLogger): OperationLogger
}