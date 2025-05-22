package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

enum class SyncOperation {

    DO_NOTHING_IN_SOURCE,
    DO_NOTHING_IN_TARGET,

    RESOLVE_COLLISION,

    COPY_FROM_SOURCE_TO_TARGET,
    COPY_FROM_TARGET_TO_SOURCE,

    DELETE_IN_SOURCE,
    DELETE_IN_TARGET,

    BACKUP_IN_SOURCE,
    BACKUP_IN_TARGET,
}