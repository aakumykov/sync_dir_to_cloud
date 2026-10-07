package com.github.aakumykov.sync_dir_to_cloud.extensions

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context

fun Context.putTextToClipboard(text: String, description: String = "plain text") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(description, text)
    clipboard.setPrimaryClip(clip)
}

fun Context.getTextFromClipboard(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val primaryClip = clipboard.primaryClip
    val primaryClipDescription = clipboard.primaryClipDescription
    return if (null != primaryClip && null != primaryClipDescription) {
        if (primaryClipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            primaryClip.getItemAt(0).text.toString()
        } else null
    } else null
}