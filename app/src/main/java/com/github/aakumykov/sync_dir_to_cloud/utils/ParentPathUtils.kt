package com.github.aakumykov.sync_dir_to_cloud.utils

import android.net.Uri
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

// TODO: протестировать!
/**
 * Вычисляет относительный путь к каталогу
 */
fun calculateRelativeParentDirPath(fsItem: FSItem, basePath: String): String {

    val itemParentPath = pathOfParentDir(fsItem.absolutePath).getOrThrow()

    return itemParentPath
        .split(FSItem.DS)
        .subtract(basePath.split(FSItem.DS).toSet())
        .joinToString(FSItem.DS)
}

/**
 * Возвращает для переданного пути файла путь к его родительскому каталогу
 * (по сути, удаляя последний сегмент этого пути).
 */
fun pathOfParentDir(path: String): Result<String> {

    if (path.isEmpty())
        return Result.failure(Exception("path is empty"))

    val pathParts = path.split(FSItem.DS).toMutableList()
    if (0 == pathParts.size)
        return Result.failure(Exception(""))

    pathParts.removeLastOrNull()

    val parentDirPath = pathParts.joinToString(FSItem.DS)

    return Result.success(parentDirPath)
}