package com.github.aakumykov.sync_dir_to_cloud.domain.entities

enum class ModificationState {
    UNCHANGED,
    SIZE_CHANGED,
    TIME_CHANGED
}