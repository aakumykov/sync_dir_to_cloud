package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.storage_dirs

import android.os.Environment
import java.io.File

val downloadsDir: File
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
