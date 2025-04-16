package com.github.aakumykov.sync_dir_to_cloud.bb_new.objects

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalTestFileManager

object LocalFileManagerHolder {
    val fileManager: LocalTestFileManager = LocalTestFileManager()
}