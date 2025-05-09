package com.github.aakumykov.sync_dir_to_cloud.functions

import java.io.File

/**
 * "Нормализует" путь к файлу/папке, прогоняя его через File(path).absolutePath
 * При этом убирает из него:
 * - конечный слеш;
 * - множественные слеши.
 */
fun normalizeAbsolutePath(path: String): String {
    val workingPath = File(path).absolutePath
    return when {
        (path.startsWith("/")) -> workingPath
        else -> workingPath.replaceFirst(Regex("^/"),"")
    }
}