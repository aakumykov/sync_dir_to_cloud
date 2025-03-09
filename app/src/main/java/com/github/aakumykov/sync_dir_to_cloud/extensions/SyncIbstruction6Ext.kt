package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6

val SyncInstruction6.isFile: Boolean get() = !isDir