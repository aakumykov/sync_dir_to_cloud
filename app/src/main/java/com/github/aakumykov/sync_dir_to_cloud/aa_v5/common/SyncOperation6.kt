package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

enum class SyncOperation6 {
    COPY_FROM_SOURCE_TO_TARGET,
    COPY_FROM_TARGET_TO_SOURCE,
    DELETE_IN_SOURCE,
    DELETE_IN_TARGET,
    RENAME_IN_SOURCE,
    RENAME_IN_TARGET,
//    BACKUP_IN_SOURCE,
//    BACKUP_IN_TARGET,
    SYNC_AGAIN
}