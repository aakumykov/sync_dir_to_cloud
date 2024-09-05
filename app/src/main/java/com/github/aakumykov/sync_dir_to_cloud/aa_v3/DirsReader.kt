package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

@Deprecated("Неудачное название")
interface DirsReader {
    fun getDeletedDirs(taskId: String): List<SyncObject>

}
