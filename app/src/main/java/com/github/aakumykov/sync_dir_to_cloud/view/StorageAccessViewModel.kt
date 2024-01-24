package com.github.aakumykov.sync_dir_to_cloud.view

import androidx.lifecycle.ViewModel
import com.github.aakumykov.single_live_event.SingleLiveEvent

class StorageAccessViewModel : ViewModel() {

    val storageAccessRequest: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val storageAccessResult: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun setStorageAccessResult(isGranted: Boolean) {
        storageAccessResult.value = isGranted
    }

    fun requestStorageAccess() {
        storageAccessRequest.value = true
    }
}