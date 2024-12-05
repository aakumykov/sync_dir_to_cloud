package com.github.aakumykov.sync_dir_to_cloud.aa_v3.file_copier

import java.util.UUID

fun createOperationId(): String = UUID.randomUUID().toString()