package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage

data class FormState(
    val isFinished: Boolean,
    val name: String?,
    val token: String?,
    val nameError: TextMessage?,
    val tokenError: TextMessage?
) {
    fun isValid(): Boolean = (null == nameError && null == tokenError)
}
