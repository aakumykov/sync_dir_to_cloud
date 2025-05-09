package com.github.aakumykov.sync_dir_to_cloud.functions

import java.io.File

fun fileNameFromPath(path: String): String = File(path).name
