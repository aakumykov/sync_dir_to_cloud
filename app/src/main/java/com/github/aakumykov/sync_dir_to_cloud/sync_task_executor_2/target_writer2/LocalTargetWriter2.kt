package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer2

import com.github.aakumykov.fs_item.FSItem
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalTargetWriter2 @AssistedInject constructor() : TargetWriter2 {

    override suspend fun createDir(fsItem: FSItem) {
        TODO("Not yet implemented")
    }

    override suspend fun uploadFile(fsItem: FSItem) {
        TODO("Not yet implemented")
    }

    @AssistedFactory
    interface Factory : TargetWriter2.Factory {
        override fun create(authToken: String): LocalTargetWriter2
    }
}