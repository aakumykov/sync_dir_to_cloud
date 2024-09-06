package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.CancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ViewModelsModule_ProvideTaskInfoViewModelFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.functions.allNotNull
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val syncObjectLogReader: SyncObjectLogReader,
    private val cancellationHolder: CancellationHolder,
)
    : ViewModel()
{
    fun getListLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return syncObjectLogReader.getListAsLiveData(taskId,executionId)
    }

    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            cancellationHolder.get(id)?.cancelAndJoin()
        }
    }
}
