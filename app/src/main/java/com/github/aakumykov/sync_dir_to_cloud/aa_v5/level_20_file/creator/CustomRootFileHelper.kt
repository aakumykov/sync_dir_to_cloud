package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.functions.basePathOf
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.functions.dirNameFromPath
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
        Log.d(TAG, "createDir($dirPath")

        val fullPath = combineFSPaths(rootPath, dirPath)
        val basePath = basePathOf(fullPath)
        val dirName = dirNameFromPath(fullPath)

        return if ("" != dirPath) cloudWriter.createDirIfNotExists(basePath, dirName)
        else dirPath
    }

    companion object {
        val TAG: String = CustomRootFileHelper::class.java.simpleName
    }
}


@AssistedFactory
interface CustomRootFileHelperAssistedFactory {
    fun create(rootPath: String,
               cloudWriter: CloudWriter): CustomRootFileHelper
}