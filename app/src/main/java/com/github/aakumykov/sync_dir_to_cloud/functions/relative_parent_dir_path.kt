package com.github.aakumykov.sync_dir_to_cloud.functions

fun relativeParentDirPath(absolutePath: String, basePath: String): String {

    val name = fileNameFromPath(absolutePath)

    return normalizeAbsolutePath(absolutePath)
        .replace(Regex("${name}$"),"")
        .replace(Regex("^${basePath}"),"")
}