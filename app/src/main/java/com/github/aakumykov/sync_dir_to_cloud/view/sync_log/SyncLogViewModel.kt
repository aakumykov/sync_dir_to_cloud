package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.functions.allNotNull
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val syncObjectLogReader: SyncObjectLogReader
) : ViewModel() {

    private val _errorMsg: MutableSharedFlow<TextMessage> = MutableSharedFlow()
    val errorMsg: SharedFlow<TextMessage> get() = _errorMsg

    private val _syncObjectInfoList: MutableLiveData<List<SyncObjectLogItem>> = MutableLiveData()
    val syncObjectInfoList get(): LiveData<List<SyncObjectLogItem>> = _syncObjectInfoList


    fun startWork(taskId: String, executionId: String) {
        allNotNull(taskId, executionId) {

            viewModelScope.launch {
                syncObjectLogReader.getList(taskId, executionId).also {
                    _syncObjectInfoList.value = it
                }
            }

        } ?: {
            sendError(TextMessage(R.string.SYNC_LOG_error_insufficient_arguments))
        }
    }

    private fun sendError(textMessage: TextMessage) {
        viewModelScope.launch { _errorMsg.emit(textMessage) }
    }
}
