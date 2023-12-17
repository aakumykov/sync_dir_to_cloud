package com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories

import com.github.aakumykov.sync_dir_to_cloud.target_witers.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.target_witers.yandex_disk.MyYandexDiskClient
import com.github.aakumykov.sync_dir_to_cloud.target_witers.yandex_disk.YandexDiskTargetWriter
import dagger.assisted.AssistedFactory

interface TargetWriterAssistedFactory {
    fun create(authToken: String): TargetWriter
}

@AssistedFactory
interface YandexTargetWriterAssistedFactory : TargetWriterAssistedFactory {
    override fun create(authToken: String): YandexDiskTargetWriter {
        return YandexDiskTargetWriter(MyYandexDiskClient(authToken));
    }
}