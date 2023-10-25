package com.github.aakumykov.sync_dir_to_cloud.view.ext_functions

import android.widget.EditText
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage

fun EditText.setError(textMessage: TextMessage) {
    error = textMessage.get(context)
}
