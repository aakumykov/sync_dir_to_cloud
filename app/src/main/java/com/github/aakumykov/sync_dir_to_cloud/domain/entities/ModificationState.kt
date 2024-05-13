package com.github.aakumykov.sync_dir_to_cloud.domain.entities

enum class ModificationState {
    UNKNOWN,
    NEW,
    DELETED,
    UNCHANGED,
    MODIFIED
}