package com.github.aakumykov.sync_dir_to_cloud.domain.entities

enum class StateInSource {
    NEW,
    DELETED,
    UNCHANGED,
    MODIFIED;
}

fun StateInSource.isNew(): Boolean = this == StateInSource.NEW