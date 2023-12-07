package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

interface FileListerFactory {
    fun create(authToken: String): FileLister
}
