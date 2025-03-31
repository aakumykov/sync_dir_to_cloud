package com.github.aakumykov.sync_dir_to_cloud.screens

import com.github.aakumykov.sync_dir_to_cloud.R
import com.kaspersky.components.kautomator.screen.UiScreen
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton

object TaskListScreen : KScreen<TaskListScreen>() {

    override val layoutId: Int?
        get() = null
    override val viewClass: Class<*>?
        get() = null

    val addTaskButton = KButton { withId(R.id.addButton) }
}