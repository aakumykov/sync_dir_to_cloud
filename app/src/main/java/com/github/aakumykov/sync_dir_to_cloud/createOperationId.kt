package com.github.aakumykov.sync_dir_to_cloud

import java.util.UUID

fun createOperationId(): String = UUID.randomUUID().toString()