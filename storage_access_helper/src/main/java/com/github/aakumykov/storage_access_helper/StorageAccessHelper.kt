package com.github.aakumykov.storage_access_helper

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.fragment.app.FragmentActivity
import dagger.assisted.AssistedFactory

interface StorageAccessHelper {

    fun requestStorageAccess(resultCallback: (isGranted: Boolean) -> Unit)

    fun hasStorageAccess(): Boolean

    fun openStorageAccessSettings()


    companion object {
        fun create(fragmentActivity: FragmentActivity): StorageAccessHelper {
            return when {
                isAndroidROrLater() -> StorageAccessHelperModern(fragmentActivity)
                else -> StorageAccessHelperLegacy(fragmentActivity)
            }
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
        private fun isAndroidROrLater() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

}