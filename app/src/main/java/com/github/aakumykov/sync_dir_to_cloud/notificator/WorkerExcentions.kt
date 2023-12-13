package com.github.aakumykov.sync_dir_to_cloud.notificator

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.Worker

fun Worker.successResult(): ListenableWorker.Result {
    return ListenableWorker.Result.success()
}

fun Worker.errorResult(errorMsg: String): ListenableWorker.Result {
    return ListenableWorker.Result.failure(errorData(errorMsg))
}

private fun errorData(errorMsg: String): Data {
    return Data.Builder()
        .apply { putString(ServiceStartingWorker.ERROR_MSG, errorMsg) }
        .build()
}

