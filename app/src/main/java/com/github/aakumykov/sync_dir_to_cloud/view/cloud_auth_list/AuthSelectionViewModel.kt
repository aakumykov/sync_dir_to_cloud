package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

class AuthSelectionViewModel : ViewModel() {

    private val _selectedCloudAuth: MutableLiveData<CloudAuth> = MutableLiveData()

    fun getSelectedCloudAuth(): LiveData<CloudAuth> = _selectedCloudAuth

    fun setSelectedCloudAuth(cloudAuth: CloudAuth) {
        _selectedCloudAuth.value = cloudAuth
    }
}
