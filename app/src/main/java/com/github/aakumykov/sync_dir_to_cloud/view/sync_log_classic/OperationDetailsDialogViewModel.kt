package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.repository.LogOfSyncRepository
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class OperationDetailsDialogViewModel(
    private val logOfSyncRepository: LogOfSyncRepository
): ViewModel() {

    private val _logOfSync: MutableSharedFlow<LogOfSync?> = MutableSharedFlow()
    val logOfSync: SharedFlow<LogOfSync?> = _logOfSync

    suspend fun startWorking(logItemAbout: LogItemAbout,
                             origLogId: String)
    {
        _logOfSync.emit(
            logOfSyncRepository.get(logItemAbout, origLogId)
        )
    }
}
