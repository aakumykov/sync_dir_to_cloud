package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

fun FSItem.Companion.pathSegmentsToPath(pathSegments: List<String>, isAbsolutePath: Boolean): String {
    return pathSegments.joinToString(DS).let { relativePath ->
        if (isAbsolutePath) DS + relativePath
        else relativePath
    }
}