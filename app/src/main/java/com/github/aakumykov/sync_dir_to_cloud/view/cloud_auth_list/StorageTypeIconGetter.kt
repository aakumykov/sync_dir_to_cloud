package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import androidx.annotation.DrawableRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType

class StorageTypeIconGetter {
    companion object {
        @DrawableRes
        fun getIconFor(storageType: StorageType?): Int {
            return when(storageType) {
                StorageType.LOCAL -> R.drawable.ic_storage_type_local
                StorageType.YANDEX_DISK -> R.drawable.ic_storage_type_yandex_disk
                else -> R.drawable.ic_storage_type_question_mark
            }
        }
    }

}
