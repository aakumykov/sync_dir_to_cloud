package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions

class SyncObjectErrorRegistration(val objectId: String, errorMsg: String): Exception(errorMsg) {
}