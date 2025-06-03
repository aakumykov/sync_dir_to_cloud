package com.github.aakumykov.sync_dir_to_cloud.functions

import java.io.File

fun combineFSPaths(parentPath: String, childPath: String): String
    = File(parentPath, childPath).absolutePath

fun fileNameFromPath(path: String): String
    = File(path).name

fun dirNameFromPath(path: String): String
    = fileNameFromPath(path)

fun basePathOf(path: String): String
    = File(path).parent ?: "/"

