package com.github.aakumykov.sync_dir_to_cloud.screens

import com.github.aakumykov.sync_dir_to_cloud.R
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton

object TaskListScreen : KScreen<TaskListScreen>() {

    override val layoutId: Int?
        get() = null
    override val viewClass: Class<*>?
        get() = null

    val createSyncTestTaskButton = KButton { withId(R.id.createSyncTestTaskButton) }
    val createMirrorTestTaskButton = KButton { withId(R.id.createMirrorTestTaskButton) }

    val recyclerView = KRecyclerView(
        builder = { withId(R.id.recyclerView) },
        itemTypeBuilder = { itemType(::TaskListItemScreen) }
    )
}