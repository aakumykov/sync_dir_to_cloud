package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType

class AuthButtonLabel private constructor() {
    companion object {
        @StringRes
        fun getFor(storageType: StorageType): Int {
            return when(storageType) {
                StorageType.YANDEX_DISK -> R.string.BUTTON_LABEL_auth_in_yandex
                StorageType.LOCAL -> R.string.BUTTON_LABEL_auth_locally
                else -> throw IllegalArgumentException("Неизвестный тип хранилища '${storageType.name}'")
            }
        }
    }

}
