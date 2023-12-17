package com.github.aakumykov.sync_dir_to_cloud.target_witers

class TargetFile (
    val isDir: Boolean,
    val name: String,
    val absolutePath: String,
    val cTime: Long
) {
    fun propertiesString(): String = "{ isDir: $isDir, name: $name, path: $absolutePath }"
}
