package com.github.aakumykov.sync_dir_to_cloud.view

import androidx.lifecycle.ViewModel
import com.github.aakumykov.single_live_event.SingleLiveEvent

class NavigationViewModel : ViewModel() {

    private val mNavigationTargetSingleLiveEvent: SingleLiveEvent<NavTarget> =
        SingleLiveEvent()

    fun getNavigationTargetEvents(): SingleLiveEvent<NavTarget> {
        return mNavigationTargetSingleLiveEvent
    }
}