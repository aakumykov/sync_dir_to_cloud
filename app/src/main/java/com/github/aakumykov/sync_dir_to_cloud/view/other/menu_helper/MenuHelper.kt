package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger

class MenuHelper (
    private val context: Context,
    @ColorRes private val topLevelIconColor: Int,
    @ColorRes private val submenuIconColor: Int
) {
    fun generateMenu(menu: Menu, customActions: Array<out CustomMenuItem>?, isSubmenu: Boolean = false) {

//        MyLogger.d(TAG, "generateMenu()...")

        customActions?.forEach { customMenuAction ->
            addItemToMenu(menu, customMenuAction, isSubmenu).also { menu ->
                customMenuAction.childItems?.also {
                    generateMenu(menu, customMenuAction.childItems, true)
                }
            }
        }
    }

    private fun addItemToMenu(
        menu: Menu,
        customMenuItem: CustomMenuItem,
        itemInSubmenu: Boolean
    ): Menu {
        val returnedMenu: Menu
        val menuItem: MenuItem

        if (null == customMenuItem.childItems) {
//            MyLogger.d(TAG, "addItemToMenu($customMenuItem)")

            returnedMenu = menu

            menuItem = menu.add(0, customMenuItem.id, 0, customMenuItem.title)

            menuItem.setOnMenuItemClickListener { item: MenuItem? ->
                customMenuItem.action.run()
                true
            }
        }
        else {
//            MyLogger.d(TAG, "adding submenu")

            val itemId: Int = customMenuItem.id
            returnedMenu = menu.addSubMenu(0, itemId, 0, customMenuItem.title)
            menuItem = menu.findItem(itemId)
        }

        if (itemInSubmenu) menuItem.icon = makeSubmenuIcon(customMenuItem.icon)
        else menuItem.icon = makeTopLevelIcon(customMenuItem.icon)

        menuItem.setShowAsAction(
            if (customMenuItem.alwaysVisible) MenuItem.SHOW_AS_ACTION_ALWAYS
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


    private fun colorizeIcon(@DrawableRes iconRes: Int, @ColorInt color: Int): Drawable? {
        val icon = ResourcesCompat.getDrawable(context.resources, iconRes, context.theme)
        icon?.setTint(color)
        return icon
    }


    private fun getColor(@ColorRes colorRes: Int): Int {
        return ResourcesCompat.getColor(context.resources, colorRes, context.theme)
    }


    companion object {
        val TAG: String = MenuHelper::class.java.simpleName
    }
}