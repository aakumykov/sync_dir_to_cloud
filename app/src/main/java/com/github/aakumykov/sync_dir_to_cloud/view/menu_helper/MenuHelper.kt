package com.github.aakumykov.sync_dir_to_cloud.view.menu_helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

class MenuHelper (
    private val context: Context,
    @ColorRes private val topLevelIconColor: Int,
    @ColorRes private val submenuIconColor: Int
) {
    fun generateMenu(
        menu: Menu,
        customActions: Array<CustomMenuAction>?,
        isSubmenu: Boolean = false
    ) {
        if (null == customActions)
            return

        for (customMenuAction in customActions) {
            if (null != customMenuAction.childItems) {
                val subMenu = addItemToMenu(menu, customMenuAction, false)
                generateMenu(subMenu, customMenuAction.childItems, true)
            }
            else
                addItemToMenu(menu, customMenuAction, isSubmenu)
        }
    }


    private fun addItemToMenu(
        menu: Menu,
        customMenuAction: CustomMenuAction,
        itemInSubmenu: Boolean
    ): Menu {

        val returnedMenu: Menu
        val menuItem: MenuItem

        if (null == customMenuAction.childItems) {
            returnedMenu = menu
            menuItem = menu.add(0, customMenuAction.itemId, 0, customMenuAction.title)
            menuItem.setOnMenuItemClickListener { item: MenuItem? ->
                customMenuAction.clickAction.run()
                true
            }
        } else {
            val itemId: Int = customMenuAction.itemId
            returnedMenu = menu.addSubMenu(0, itemId, 0, customMenuAction.title)
            menuItem = menu.findItem(itemId)
        }

        if (itemInSubmenu) menuItem.icon = makeSubmenuIcon(customMenuAction.icon)
        else menuItem.icon = makeTopLevelIcon(customMenuAction.icon)

        menuItem.setShowAsAction(
            if (customMenuAction.alwaysVisible) MenuItem.SHOW_AS_ACTION_ALWAYS
            else MenuItem.SHOW_AS_ACTION_NEVER
        )

        return returnedMenu
    }


    private fun makeTopLevelIcon(@DrawableRes iconRes: Int): Drawable? {
        return colorizeIcon(iconRes, getColor(topLevelIconColor))
    }


    private fun makeSubmenuIcon(@DrawableRes iconRes: Int): Drawable? {
        return colorizeIcon(iconRes, getColor(submenuIconColor))
    }


    private fun colorizeIcon(
        @DrawableRes iconRes: Int,
        @ColorInt color: Int
    ): Drawable? {
        val icon = ResourcesCompat.getDrawable(context.resources, iconRes, context.theme)
        icon?.setTint(color)
        return icon
    }


    private fun getColor(@ColorRes colorRes: Int): Int {
        return ResourcesCompat.getColor(context.resources, colorRes, context.theme)
    }


    /*fun updateItem(
        menu: Menu?,
        @IdRes itemId: Int,
        @DrawableRes iconRes: Int,
        @StringRes titleRes: Int,
        onClickRunnable: Runnable
    ) {
        if (null == menu) return
        val menuItem = menu.findItem(itemId) ?: return
        menuItem.setTitle(titleRes)
        menuItem.setIcon(iconRes)
        menuItem.setOnMenuItemClickListener { item: MenuItem? ->
            onClickRunnable.run()
            true
        }
    }*/
}