package com.github.aakumykov.sync_dir_to_cloud.target_witers.yandex_disk

import com.github.aakumykov.sync_dir_to_cloud.target_witers.TargetWriter
import com.github.aakumykov.yandex_disk_client.YandexDiskClient
import java.io.File

class YandexDiskTargetWriter(
    private val yandexDiskClient: MyYandexDiskClient
) : TargetWriter {

    override suspend fun writeFile(file: File, path: String) {
        
    }

    override suspend fun createDir(path: String) {

    }
}