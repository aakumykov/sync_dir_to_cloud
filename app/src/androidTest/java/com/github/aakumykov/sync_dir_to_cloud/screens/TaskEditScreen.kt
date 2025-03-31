package com.github.aakumykov.sync_dir_to_cloud.screens

import com.github.aakumykov.sync_dir_to_cloud.R
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView

object TaskEditScreen : KScreen<TaskEditScreen>() {

    override val layoutId: Int?
        get() = null
    override val viewClass: Class<*>?
        get() = null


    val sourcePathInput = KEditText { withId(R.id.sourcePathInput) }
    val targetPathInput = KEditText { withId(R.id.targetPathInput) }

    val sourcePathSelectionButton = KButton { withId(R.id.sourcePathSelectionButton)}
    val targetPathSelectionButton = KButton { withId(R.id.targetPathSelectionButton)}

    val saveButton = KButton { withId(R.id.saveButton)}

    val addStorageButton = KButton { withId(R.id.addButton) }
    val localStorageSubbutton = KTextView { withText(R.string.speed_dial_auth_label_local) }
}