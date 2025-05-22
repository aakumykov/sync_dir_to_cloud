package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator

import com.github.aakumykov.cloud_writer.CloudWriter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class CustomRootFileHelper @AssistedInject constructor(
    @Assisted private val rootPath: String,
    @Assisted private val cloudWriter: CloudWriter,
){
    /**
     * @param dirPath Relative dir path (with or without starting slash).
     * @return Absolute path to created dir.
     */
    fun createDir(dirPath: String): String {
        return cloudWriter.createDir(
            rootPath,
            dirPath
        )
    }
}


@AssistedFactory
interface CustomRootFileHelperAssistedFactory {
    fun create(rootPath: String,
               cloudWriter: CloudWriter): CustomRootFileHelper
}