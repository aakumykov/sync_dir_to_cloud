package com.github.aakumykov.sync_dir_to_cloud.domain.entities

enum class StateInStorage {
    NEW,
    DELETED,
    UNCHANGED,
    MODIFIED;
}

fun StateInStorage.isNew(): Boolean = this == StateInStorage.NEW