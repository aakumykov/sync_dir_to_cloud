package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo

open class SyncTaskBase (
    @ColumnInfo(name = "source_path") val sourcePath: String,
    @ColumnInfo(name = "target_path") val targetPath: String,
)
