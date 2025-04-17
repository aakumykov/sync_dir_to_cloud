package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import java.util.UUID
import kotlin.random.Random

fun randomBytes(count: Int): ByteArray = Random.nextBytes(count)

val randomName: String
    get() = UUID.randomUUID().toString().split("-")[0]