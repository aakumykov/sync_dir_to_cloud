package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.FailedValidationResult
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.SuccessValidationResult
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.ValidationResult
import kotlinx.coroutines.launch

class AuthEditViewModel(application: Application) : AndroidViewModel(application) {

    private val cloudAuthAdder = App.getAppComponent().getCloudAuthAdder()
    private val cloudAuthChecker = App.getAppComponent().getCloudAuthChecker()

    private val _nameValidationResult: MutableLiveData<ValidationResult> = MutableLiveData()
    val nameValidationResult get(): LiveData<ValidationResult> = _nameValidationResult

    private val _tokenValidationResult: MutableLiveData<ValidationResult> = MutableLiveData()
    val tokenValidationResult get(): LiveData<ValidationResult> = _nameValidationResult


    fun createCloudAuth(authName: String, authToken: String) {
        viewModelScope.launch {
            if (nameIsValid() && tokenIsValid()) {
                _nameValidationResult.value = checkName(authName)
                _tokenValidationResult.value = checkToken(authToken)
                cloudAuthAdder.addCloudAuth(CloudAuth(authName, authToken))
            }
        }
    }

    private suspend fun checkToken(authToken: String): ValidationResult {
        authToken.isEmpty().let {
            return FailedValidationResult(R.string.VALIDATION_token_is_empty)
        }
    }

    private suspend fun checkName(authName: String): ValidationResult {
        if (cloudAuthChecker.hasAuthWithName(authName))
            return FailedValidationResult(R.string.VALIDATION_name_already_used)
        else
            return SuccessValidationResult()
    }

    private fun tokenIsValid(): Boolean {
        return tokenValidationResult.value is SuccessValidationResult
    }

    private fun nameIsValid(): Boolean {
        return nameValidationResult.value is SuccessValidationResult
    }
}