package com.github.aakumykov.storage_access_helper

import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity

class StorageAccessHelperModern(private val activity: FragmentActivity): StorageAccessHelper {

    // TODO: лениво
    private val activityResultLauncher: ActivityResultLauncher<Unit>
    private var onResult: ((isGranted: Boolean) -> Unit)? = null


    init {
        activityResultLauncher = activity.registerForActivityResult(ManageAllFilesContract(activity.packageName)) { isGranted ->
            invokeOnResult(isGranted)
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun requestStorageAccess(resultCallback: (isGranted: Boolean) -> Unit) {
        this.onResult = resultCallback

        if (hasStorageAccess())
            invokeOnResult(true)
        else
            activityResultLauncher.launch(Unit)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun hasStorageAccess(): Boolean = hasStorageAccessModern()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun openStorageAccessSettings() {
        activity.startActivity(IntentHelper.manageAllFilesIntent(activity))
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasStorageAccessModern(): Boolean = Environment.isExternalStorageManager()

    private fun invokeOnResult(isGranted: Boolean) = onResult?.invoke(isGranted)
}