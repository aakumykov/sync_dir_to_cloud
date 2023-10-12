package com.github.aakumykov.sync_dir_to_cloud.view.common_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class PageTitleViewModel(application: Application) : AndroidViewModel(application) {

    private val pageTitleLiveData = MutableLiveData<String>()

    fun getPageTitle(): LiveData<String> = pageTitleLiveData

    fun setPageTitle(title: String) {
        pageTitleLiveData.postValue(title)
    }
}