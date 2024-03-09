package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

class MenuState(vararg initialMenuItems: CustomMenuItem) {
    val menuItems: Array<out CustomMenuItem>
    init {
        menuItems = initialMenuItems
    }
}

class MenuState2 {

}