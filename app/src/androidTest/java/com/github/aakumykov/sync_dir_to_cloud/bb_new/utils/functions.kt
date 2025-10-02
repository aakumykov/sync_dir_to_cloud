package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import java.util.UUID
import kotlin.random.Random

fun randomBytes(count: Int = 1024): ByteArray = Random.nextBytes(count)

val randomBytes: ByteArray get() = randomBytes()

val randomName: String
    get() = UUID.randomUUID().toString().split("-")[0]