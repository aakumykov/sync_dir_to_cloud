package com.github.aakumykov.storage_access_helper

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.fragment.app.FragmentActivity

interface StorageAccessHelper {

    fun requestStorageAccess(resultCallback: (isGranted: Boolean) -> Unit)

    fun hasStorageAccess(): Boolean

    fun openStorageAccessSettings()


    companion object {
        fun create(componentActivity: FragmentActivity): StorageAccessHelper {
            return when {
                isAndroidROrLater() -> StorageAccessHelperModern(componentActivity)
                else -> StorageAccessHelperLegacy(componentActivity)
            }
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
        private fun isAndroidROrLater() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}