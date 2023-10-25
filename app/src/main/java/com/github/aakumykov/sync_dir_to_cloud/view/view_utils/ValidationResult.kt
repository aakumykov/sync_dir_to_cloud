package com.github.aakumykov.sync_dir_to_cloud.view.view_utils

import androidx.annotation.StringRes

sealed class ValidationResult (
    val error: TextMessage?
)

class SuccessValidationResult : ValidationResult(null)

class FailedValidationResult(error: TextMessage) : ValidationResult(error) {
    constructor(@StringRes errorMessage: Int) : this(TextMessage(errorMessage))
}