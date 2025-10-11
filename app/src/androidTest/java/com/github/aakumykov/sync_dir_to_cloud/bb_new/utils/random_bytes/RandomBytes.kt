package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes

import kotlin.random.Random

val randomBytes: ByteArray get() = randomBytes()

fun randomBytes(count: Int = 10): ByteArray = Random.nextBytes(count)