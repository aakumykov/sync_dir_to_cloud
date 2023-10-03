package com.github.aakumykov.sync_dir_to_cloud.view

import androidx.lifecycle.ViewModel
import com.github.aakumykov.single_live_event.SingleLiveEvent

class NavigationViewModel : ViewModel() {

    private val navigationTargetSingleLiveEvent: SingleLiveEvent<NavTarget> =
        SingleLiveEvent()

    init {
        navigationTargetSingleLiveEvent.value = NavTarget.Start
    }

    fun getNavigationTargetEvents(): SingleLiveEvent<NavTarget> {
        return navigationTargetSingleLiveEvent
    }

    fun navigateTo(navTarget: NavTarget) {
        navigationTargetSingleLiveEvent.value = navTarget
    }
}