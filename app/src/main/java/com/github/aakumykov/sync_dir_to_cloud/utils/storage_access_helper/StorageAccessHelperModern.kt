package com.github.aakumykov.sync_dir_to_cloud.utils.storage_access_helper

import android.os.Build
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.github.aakumykov.sync_dir_to_cloud.utils.storage_access_helper.ManageAllFilesContract
import com.github.aakumykov.sync_dir_to_cloud.utils.storage_access_helper.StorageAccessHelper

class StorageAccessHelperModern(activity: ComponentActivity): StorageAccessHelper {

    // TODO: лениво
    private val activityResultLauncher: ActivityResultLauncher<Unit>
    private var onResult: ((isGranted: Boolean) -> Unit)? = null


    init {
        activityResultLauncher = activity.registerForActivityResult(ManageAllFilesContract(activity.packageName)) { result ->
            onResult?.invoke(result)
        }
    }

    override fun requestStorageAccess(resultCallback: (isGranted: Boolean) -> Unit) {
        this.onResult = resultCallback
        activityResultLauncher.launch(Unit)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun hasStorageAccess(): Boolean = hasStorageAccessModern()


    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasStorageAccessModern(): Boolean = Environment.isExternalStorageManager()
}