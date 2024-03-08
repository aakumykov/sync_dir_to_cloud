package com.github.aakumykov.sync_dir_to_cloud.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.single_live_event.SingleLiveEvent
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.CustomMenuItem

typealias MenuState = Array<CustomMenuItem>

class MenuStateViewModel : ViewModel() {

    private val _menuState: SingleLiveEvent<MenuState> = SingleLiveEvent()
    val menuState: LiveData<MenuState> = _menuState

    fun sendMenuState(menuState: MenuState) {
        _menuState.value = menuState
    }
}
