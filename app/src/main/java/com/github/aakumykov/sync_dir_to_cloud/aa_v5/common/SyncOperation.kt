package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

enum class SyncOperation {
    DO_NOTHING,
    COPY,
    BACKUP_COPY,
    DELETE,
    BACKUP_DELETE,
}