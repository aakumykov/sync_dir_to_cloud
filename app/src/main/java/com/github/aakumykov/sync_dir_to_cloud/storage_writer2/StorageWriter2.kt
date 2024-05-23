package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import java.io.InputStream

interface StorageWriter2 {

    /**
     * @return Полное имя (т.е. путь) созданного каталогу, обёрнутое в Result.
     */
    suspend fun createDir(basePath: String, dirName: String): Result<String>

    /**
     * @return Полное имя (т.е. путь) записанного файла, обёрнутое в Result.
     */
    suspend fun putFile(
        sourceFileInputStream: InputStream?,
        targetFilePath: String?,
        overwriteIfExists: Boolean = false
    ): Result<String>
}


