package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.widget.EditText
import android.widget.TextView
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage

fun EditText.setError(textMessage: TextMessage) {
    error = textMessage.get(context)
}

fun TextView.setText(textMessage: TextMessage) {
    text = textMessage.get(context)
}