package com.github.aakumykov.sync_dir_to_cloud.screens

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.list.KAbsListView

object CloudAuthListScreen : KScreen<CloudAuthListScreen>() {

    override val layoutId: Int?
        get() = null
    override val viewClass: Class<*>?
        get() = null

    val listView = KAbsListView(
        builder = {},
        itemTypeBuilder = {}
    )
}