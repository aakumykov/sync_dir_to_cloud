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
    @ColorRes private val menuIconColor: Int,
    @ColorRes private val submenuIconColor: Int
) {
    fun generateMenu(
        menu: Menu,
        customActions: Array<CustomMenuAction>,
        isSubmenu: Boolean
    ) {
        for (customMenuAction in customActions) {
            if (null != customMenuAction.childActions) {
                val subMenu = addItemToMenu(context, menu, customMenuAction, false)
                generateMenu(subMenu, customMenuAction.childActions, true)
            } else addItemToMenu(context, menu, customMenuAction, isSubmenu)
        }
    }


    private fun addItemToMenu(
        context: Context,
        menu: Menu,
        customMenuAction: CustomMenuAction,
        itemInSubmenu: Boolean
    ): Menu {

        val returnedMenu: Menu
        val menuItem: MenuItem
        val isSubmenu = null != customMenuAction.childActions

        if (isSubmenu) {
            val itemId: Int = customMenuAction.itemId
            returnedMenu = menu.addSubMenu(0, itemId, 0, customMenuAction.titleRes)
            menuItem = menu.findItem(itemId)
        } else {
            returnedMenu = menu
            menuItem = menu.add(0, customMenuAction.itemId, 0, customMenuAction.titleRes)
            menuItem.setOnMenuItemClickListener { item: MenuItem? ->
                customMenuAction.clickRunnable.run()
                true
            }
        }

        if (itemInSubmenu) menuItem.icon = makeSubmenuIcon(customMenuAction.iconRes)
        else menuItem.icon = makeTopLevelIcon(customMenuAction.iconRes)

        menuItem.setShowAsAction(
            if (customMenuAction.alwaysVisible) MenuItem.SHOW_AS_ACTION_ALWAYS
            else MenuItem.SHOW_AS_ACTION_NEVER
        )

        return returnedMenu
    }


    private fun makeTopLevelIcon(@DrawableRes iconRes: Int): Drawable? {
        return colorizeIcon(iconRes, getColor(menuIconColor))
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


    fun updateItem(
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
    }


    private fun getColor(@ColorRes colorRes: Int): Int {
        return ResourcesCompat.getColor(context.resources, colorRes, context.theme)
    }
}