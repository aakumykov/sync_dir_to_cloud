package com.github.aakumykov.sync_dir_to_cloud.domain.entities

enum class ModificationState {
    NEW,
    DELETED,
    UNCHANGED,
    MODIFIED;
}

fun ModificationState.isNew(): Boolean = this == ModificationState.NEW