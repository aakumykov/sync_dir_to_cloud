package com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.local_file_lister.LocalFileLister
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileListerFactory
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LocalFileListerAssistedFactory : FileListerFactory {
    override fun create(cloudAuth: CloudAuth): FileLister {
        return LocalFileLister()
    }
}