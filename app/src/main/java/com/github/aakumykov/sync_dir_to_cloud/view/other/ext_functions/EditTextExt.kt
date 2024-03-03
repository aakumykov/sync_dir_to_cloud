package com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions

import android.widget.EditText
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage

fun EditText.setError(textMessage: TextMessage) {
    error = textMessage.get(context)
}
