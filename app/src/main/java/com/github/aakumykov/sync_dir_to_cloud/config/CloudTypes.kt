package com.github.aakumykov.sync_dir_to_cloud.config

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage

class CloudType private constructor() {

    private lateinit var id: String
    private lateinit var name: TextMessage

    companion object {
        const val YANDEX_DISK = "YANDEX_DISK"
        const val GOOGLE_DRIVE = "GOOGLE_DRIVE"

        val list = listOf(
            CloudType(YANDEX_DISK, TextMessage(R.string.CLOUD_TYPE_NAME_yandex_disk)),
            CloudType(YANDEX_DISK, TextMessage(R.string.CLOUD_TYPE_NAME_google_drive)),
        )
    }

    constructor(cloudTypeId: String, cloudTypeName: TextMessage) : this() {
        this.id = cloudTypeId
        this.name = cloudTypeName
    }
}