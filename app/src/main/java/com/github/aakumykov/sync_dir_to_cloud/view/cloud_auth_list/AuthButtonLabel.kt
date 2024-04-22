package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType

class AuthButtonLabel(private val resources: Resources) {

    fun getFor(storageType: StorageType): String {
        return when(storageType) {
            StorageType.YANDEX_DISK -> R.string.auth_suffix_yandex
            StorageType.LOCAL -> R.string.auth_suffix_local
        }.let { suffix ->
            resources.getString(
                R.string.BUTTON_LABEL_auth_prefix,
                resources.getString(suffix)
            )
        }
    }}
