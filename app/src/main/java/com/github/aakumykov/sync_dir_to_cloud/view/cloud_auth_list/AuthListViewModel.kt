package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.app.Application
import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpStateViewModel

class AuthListViewModel(application: Application) : OpStateViewModel(application) {

    private val cloudAuthLister = App.getAppComponent().getCloudAuthLister()

    suspend fun getAuthList(): LiveData<List<CloudAuth>>
        = cloudAuthLister.listCloudAuth()


}