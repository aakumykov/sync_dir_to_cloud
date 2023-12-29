package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer2

import com.github.aakumykov.file_uploader.OkhttpFileUploader
import com.github.aakumykov.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.yandex_disk_upload_client.YandexDiskUploadClient
import com.github.aakumykov.yandex_disk_client.YandexDiskClient
import com.github.aakumykov.yandex_disk_file_lister.FileListerYandexDiskClient
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class YandexTargetWriter2 @AssistedInject constructor(
    private val authToken: String,
    private val okhttpFileUploader: OkhttpFileUploader,
    private val yandexDiskUploadClient: YandexDiskUploadClient
): TargetWriter2 {

    // FIXME: здесь нужен относительный путь!
    override suspend fun createDir(fsItem: FSItem) {
        yandexDiskUploadClient.createDir(fsItem)
    }

    override suspend fun uploadFile(fsItem: FSItem) {
        val uploadTarget: String = yandexDiskUploadClient.getLinkForUploading(fsItem)
        okhttpFileUploader.postFileSuspend(File(fsItem.absolutePath), uploadTarget)
    }

    @AssistedFactory
    interface Factory : TargetWriter2.Factory {
        override fun create(authToken: String): YandexTargetWriter2
    }
}