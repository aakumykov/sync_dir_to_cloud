package com.github.aakumykov.sync_dir_to_cloud.enums

enum class SyncMode {
    SYNC, // Приёмник приводится в состояние, соответствующее источнику.
    MIRROR; // Источник и приёмник повторяют друг друга.
}
