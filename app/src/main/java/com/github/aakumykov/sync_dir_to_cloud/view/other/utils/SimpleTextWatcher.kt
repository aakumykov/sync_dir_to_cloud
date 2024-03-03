package com.github.aakumykov.sync_dir_to_cloud.view.other.utils

import android.text.Editable
import android.text.TextWatcher

abstract class SimpleTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    abstract override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)

    override fun afterTextChanged(s: Editable?) {}
}