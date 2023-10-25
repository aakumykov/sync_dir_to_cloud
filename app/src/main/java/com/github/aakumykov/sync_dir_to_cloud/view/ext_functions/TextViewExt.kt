package com.github.aakumykov.sync_dir_to_cloud.view.ext_functions

import android.widget.TextView
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage

fun TextView.setText(textMessage: TextMessage) {
    text = textMessage.get(context)
}