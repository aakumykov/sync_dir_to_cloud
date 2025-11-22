package com.github.aakumykov.sync_dir_to_cloud

import java.util.UUID

@Deprecated("Переименовать в randomId")
val randomUUID: String get() = UUID.randomUUID().toString()