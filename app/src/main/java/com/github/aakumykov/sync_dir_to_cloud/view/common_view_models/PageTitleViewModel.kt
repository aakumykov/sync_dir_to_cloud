package com.github.aakumykov.sync_dir_to_cloud.view.common_view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class PageTitleViewModel : ViewModel() {

    private val pageTitleLiveData = MutableLiveData<String>()

    fun getPageTitle(): LiveData<String> = pageTitleLiveData

    fun setPageTitle(title: String) {
        pageTitleLiveData.postValue(title)
    }
}