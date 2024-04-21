package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthAdder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// FIXME: использовать отдельные интерфейсы или в виде CloudAuthManagingUseCase ?

class CloudAuthEditViewModel(
    private val cloudAuthAdder: CloudAuthAdder,
//    private val cloudAuthReader: CloudAuthReader,
) : ViewModel() {

    private val _result: MutableLiveData<Result<CloudAuth>> = MutableLiveData()
    val authCreationResult: LiveData<Result<CloudAuth>> = _result

    fun createCloudAuth(name: String, authToken: String) {
        viewModelScope.launch {
            try {
                CloudAuth(name, authToken).also {

                    withContext(Dispatchers.IO) {
                        cloudAuthAdder.addCloudAuth(it)
                    }

                    _result.value = Result.success(it)
                }
            } catch (throwable: Throwable) {
                _result.value = Result.failure(throwable)
            }
        }
    }
}