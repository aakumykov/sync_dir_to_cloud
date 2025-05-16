package com.github.aakumykov.sync_dir_to_cloud.utils

import androidx.lifecycle.Lifecycling
import java.util.Date

@Deprecated("Переделать в свойство")
fun currentTime(): Long = Date().time