package com.github.aakumykov.sync_dir_to_cloud.screens

import com.github.aakumykov.sync_dir_to_cloud.R
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.text.KButton

object CloudAuthEditScreen : KScreen<CloudAuthEditScreen>() {

    override val layoutId: Int?
        get() = null
    override val viewClass: Class<*>?
        get() = null

    val nameInput = KEditText { withId(R.id.nameView) }
    val saveButton = KButton { withId(R.id.saveButton) }
}