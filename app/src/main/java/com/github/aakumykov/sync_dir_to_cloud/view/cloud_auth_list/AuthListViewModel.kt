package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.PageOpStateViewModel

typealias AuthList = List<CloudAuth>

class AuthListViewModel(application: Application) : PageOpStateViewModel(application) {

    private val cloudAuthLister = App.getAppComponent().getCloudAuthLister()
    private val _authListMediatorLiveData: MediatorLiveData<AuthList> = MediatorLiveData()
    val authList: LiveData<AuthList> get() = _authListMediatorLiveData

    suspend fun startLoadingList() {
        _authListMediatorLiveData.addSource(cloudAuthLister.listCloudAuth()) {
            _authListMediatorLiveData.setValue(it)
        }
    }
}