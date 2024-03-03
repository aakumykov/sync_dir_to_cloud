package com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions

import android.widget.TextView
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage

fun TextView.setText(textMessage: TextMessage) {
    text = textMessage.get(context)
}