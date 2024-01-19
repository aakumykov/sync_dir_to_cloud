package com.github.aakumykov.sync_dir_to_cloud.utils

import android.net.Uri
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

// TODO: протестировать!
/**
 * Вычисляет относительный путь к каталогу
 */
fun calculateRelativeParentDirPath(fsItem: FSItem, basePath: String): String {

    val itemParentPath = pathOfParentDir(fsItem.absolutePath)

    return itemParentPath
        .split(FSItem.DS)
        .subtract(basePath.split(FSItem.DS).toSet())
        .joinToString(FSItem.DS)
}

/**
 * Возвращает для переданного пути файла путь к его родительскому каталогу
 * (по сути, удаляя последний сегмент этого пути).
 */
@Deprecated("Некорректная реализация, переписать и протестировать!")
private fun pathOfParentDir(path: String): String {
    return with(Uri.parse(path).pathSegments.toMutableList()) {
        removeLast()
        joinToString(FSItem.DS)
    }
}