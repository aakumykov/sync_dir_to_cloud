package com.github.aakumykov.sync_dir_to_cloud.functions

import java.io.File

fun relativeParentDirPath(absolutePath: String, basePath: String): String {

    val normalizedAbsolutePath = File(absolutePath).absolutePath

    val name = fileNameFromPath(absolutePath)

    return normalizedAbsolutePath
        .replace(Regex("${name}$"),"")
        .replace(Regex("^${basePath}"),"")
}