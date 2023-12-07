package com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileListerFactory
import dagger.assisted.AssistedFactory

@AssistedFactory
interface QwertyFileListerAssistedFactory : FileListerFactory {
    override fun create(authToken: String): QwertyFileLister {
        return QwertyFileLister(authToken)
    }
}