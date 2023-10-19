package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

interface CloudAuthSetter {
    fun setCloudAuth(cloudAuth: CloudAuth)
}
