package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.functions.allNotNull
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import com.github.aakumykov.sync_dir_to_cloud.utils.CancelHolder
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val syncObjectLogReader: SyncObjectLogReader,
    private val cancelHolder: CancelHolder
)
    : ViewModel()
{
    fun getListLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return syncObjectLogReader.getListAsLiveData(taskId,executionId)
    }

    fun cancelOperation(operationId: String) {
        viewModelScope.launch {
            cancelHolder.getCancelHandler(operationId)
                ?.also {
                    it.cancelAndJoin()
                }
                ?.also {
                    cancelHolder.removeHandler(operationId)
                } ?: {
                    Log.e(TAG, "не найдено")
                }
        }
    }

    companion object {
        val TAG: String = SyncLogViewModel::class.java.simpleName
    }
}
