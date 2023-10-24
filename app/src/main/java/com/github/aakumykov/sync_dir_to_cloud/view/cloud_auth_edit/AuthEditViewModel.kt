package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import kotlinx.coroutines.launch

class AuthEditViewModel(application: Application) : AndroidViewModel(application) {

    private val cloudAuthAdder = App.getAppComponent().getCloudAuthAdder()

    // FIXME: проверка корректности полей
    fun createCloudAuth(authName: String, authToken: String) {
        viewModelScope.launch {
            cloudAuthAdder.addCloudAuth(CloudAuth(authName, authToken))
        }
    }
}