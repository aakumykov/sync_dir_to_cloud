package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

interface AuthSelectionDialog {

    fun setCallback(callback: Callback)
    fun unsetCallback()

    interface Callback {
        fun onCloudAuthSelected(cloudAuth: CloudAuth)
    }
}
