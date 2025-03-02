package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

enum class SyncOperation {
    DO_NOTHING,
    BACKUP,
    COPY,
    DELETE,
    RENAME_WITH_SUFFIX,
    COPY_RENAMED
}