package com.github.aakumykov.sync_dir_to_cloud.target_witers

import java.io.File

interface TargetWriter {
    suspend fun writeFile(file: File, path: String)
    suspend fun createDir(path: String)
}