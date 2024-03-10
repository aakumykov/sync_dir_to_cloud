package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

@Deprecated("Перехожу на FragmentResultAPI")
interface AuthSelectionDialog {

    @Deprecated("Перехожу на FragmentResultAPI")
    fun setCallback(callback: Callback)

    @Deprecated("Перехожу на FragmentResultAPI")
    fun unsetCallback()

    @Deprecated("Перехожу на FragmentResultAPI")
    interface Callback {
        @Deprecated("Перехожу на FragmentResultAPI")
        fun onCloudAuthSelected(cloudAuth: CloudAuth)
    }
}
