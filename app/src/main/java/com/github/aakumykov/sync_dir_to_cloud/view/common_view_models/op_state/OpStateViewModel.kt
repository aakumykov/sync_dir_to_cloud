package com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class OpStateViewModel(application: Application) : AndroidViewModel(application) {

    private val opStateMutableLiveData: MutableLiveData<OpState> = MutableLiveData()

    fun getOpState(): LiveData<OpState> = opStateMutableLiveData

    protected fun setOpState(opState: OpState) {
        opStateMutableLiveData.postValue(opState)
    }
}