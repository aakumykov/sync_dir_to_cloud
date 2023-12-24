package com.github.aakumykov.sync_dir_to_cloud.utils

import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

fun sha256byteArray(str: String): ByteArray = MessageDigest.getInstance("SHA-256")
    .digest(str.toByteArray(UTF_8))

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

fun sha256(str: String) = sha256byteArray(str).toHex()