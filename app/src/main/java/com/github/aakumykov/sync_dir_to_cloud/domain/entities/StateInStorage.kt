package com.github.aakumykov.sync_dir_to_cloud.domain.entities

enum class StateInStorage {
    UNCHANGED,
    NEW,
    MODIFIED,
    DELETED
}

fun StateInStorage.isNew(): Boolean = this == StateInStorage.NEW
fun StateInStorage.isModified(): Boolean = this == StateInStorage.MODIFIED