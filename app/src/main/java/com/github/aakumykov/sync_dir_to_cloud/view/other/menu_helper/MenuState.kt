package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

class MenuState(vararg initialMenuItems: CustomMenuItem) {

    val menuItems: Array<out CustomMenuItem> get() = menuMap.values.toTypedArray()

    private val menuMap: MutableMap<Int, CustomMenuItem> = mutableMapOf()

    init {
        initialMenuItems.forEach { menuMap[it.id] = it }
    }

    fun updateIcon(@IdRes id: Int, @DrawableRes icon: Int): MenuState = updateItem(id,icon,null,null)

    fun updateTitle(@IdRes id: Int, @StringRes title: Int): MenuState = updateItem(id,null,title,null)

    fun updateAction(@IdRes id: Int, action: Runnable): MenuState = updateItem(id, null, null, action)

    fun updateItem(
        @IdRes id: Int,
        @DrawableRes icon: Int?,
        @StringRes title: Int? = null,
        action: Runnable? = null,
    ): MenuState {
        menuMap[id]?.also { customMenuItem ->
            icon?.also { customMenuItem.icon = it }
            title?.also { customMenuItem.title = it }
            action?.also { customMenuItem.action = it }
        }
        return this
    }

    companion object {
        fun noMenu(): MenuState = MenuState()
    }
}
