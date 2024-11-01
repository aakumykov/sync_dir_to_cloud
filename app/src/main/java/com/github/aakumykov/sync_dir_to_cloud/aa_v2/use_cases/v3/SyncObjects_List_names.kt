package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

val List<SyncObject>.names: String get() = joinToString(", ") { it.name }