package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.fs_item_ext

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

fun FSItem.Companion.pathSegmentsToPath(pathSegments: List<String>, isAbsolutePath: Boolean): String {
    return pathSegments.joinToString(DS).let { relativePath ->
        if (isAbsolutePath) DS + relativePath
        else relativePath
    }
}