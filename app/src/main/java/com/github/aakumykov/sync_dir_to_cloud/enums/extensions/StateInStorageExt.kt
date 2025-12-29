package com.github.aakumykov.sync_dir_to_cloud.enums.extensions

import com.github.aakumykov.sync_dir_to_cloud.enums.StateInStorage

val StateInStorage.isNew: Boolean get() = this == StateInStorage.NEW

val StateInStorage.isNewOrIsModified: Boolean get() {
    return StateInStorage.MODIFIED == this ||
            StateInStorage.NEW == this
}