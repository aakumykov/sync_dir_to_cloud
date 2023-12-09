package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.utils.TextMessage
import kotlinx.coroutines.launch

class AuthEditViewModel(application: Application) : AndroidViewModel(application) {

    private val cloudAuthManagingUseCase = App.getAppComponent().getCloudAuthManagingUseCase()

    private val _formState: MutableLiveData<FormState> = MutableLiveData()
    val formState: LiveData<FormState> get() = _formState


    fun createCloudAuth(authName: String, authToken: String) {
        viewModelScope.launch {

            val formState = FormState(false,
                authName,
                authToken,
                checkName(authName),
                checkToken(authToken)
            )

            if (formState.isValid())
                createCloudAuthReal(formState)
            else
                _formState.value = formState
        }
    }

    private suspend fun createCloudAuthReal(formState: FormState) {
        cloudAuthManagingUseCase.addCloudAuth(CloudAuth(formState.name!!, formState.token!!))
        _formState.value = formState.copy(isFinished = true)
    }

    private fun checkToken(authToken: String): TextMessage? {
        return if (authToken.isEmpty())
            TextMessage(R.string.VALIDATION_cloud_authorization_required)
        else null
    }

    private suspend fun checkName(authName: String): TextMessage? {

        if (authName.isEmpty())
            return TextMessage(R.string.VALIDATION_cannot_be_empty)

        return if (cloudAuthManagingUseCase.hasCloudAuth(authName))
            TextMessage(R.string.VALIDATION_name_already_used)
        else null
    }
}