package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.target_context.targetContext
import java.io.File

val cacheDir: File
    get() = targetContext.cacheDir