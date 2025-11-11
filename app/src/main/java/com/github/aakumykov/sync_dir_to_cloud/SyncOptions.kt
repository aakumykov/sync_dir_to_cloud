package com.github.aakumykov.sync_dir_to_cloud

import javax.inject.Inject

class SyncOptions @Inject constructor() {
    val chunkSize: Int = 3
    val overwriteIfExists: Boolean = true
}