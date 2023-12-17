package com.github.aakumykov.sync_dir_to_cloud.target_witers.yandex_disk

import com.github.aakumykov.sync_dir_to_cloud.enums.SortingMode
import com.github.aakumykov.sync_dir_to_cloud.target_witers.TargetFile
import com.github.aakumykov.yandex_disk_client.YandexDiskClient
import com.github.aakumykov.yandex_disk_client.YandexDiskSortingMode
import com.yandex.disk.rest.json.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MyYandexDiskClient @AssistedInject constructor(
    @Assisted authToken: String
)
    : YandexDiskClient<TargetFile, SortingMode>(authToken)
{
    override fun appToDiskSortingMode(appSortingMode: SortingMode): YandexDiskSortingMode {
        return when(appSortingMode) {
            SortingMode.BY_NAME_DIRECT -> YandexDiskSortingMode.NAME_DIRECT
            else -> YandexDiskSortingMode.NAME_REVERSE
        }
    }

    override fun cloudItemToLocalItem(resource: Resource): TargetFile
        = yandexDiskResourceToTargetFile(resource)

    override fun cloudFileToString(resource: Resource): String {
        val targetFile = yandexDiskResourceToTargetFile(resource)
        return "YandexDisk Resource ${targetFile.propertiesString()}"
    }

    private fun yandexDiskResourceToTargetFile(resource: Resource): TargetFile {
        return TargetFile(
            resource.isDir,
            resource.name,
            resource.path.path,
            resource.created.time
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(authToken: String): MyYandexDiskClient
    }
}