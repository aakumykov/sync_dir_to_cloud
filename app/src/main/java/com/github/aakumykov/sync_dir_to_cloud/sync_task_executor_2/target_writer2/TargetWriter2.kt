package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer2

import com.github.aakumykov.fs_item.FSItem

interface TargetWriter2 {
    suspend fun createDir(fsItem: FSItem)
    suspend fun uploadFile(fsItem: FSItem)

    interface Factory {
        fun create(authToken: String): TargetWriter2
    }
}