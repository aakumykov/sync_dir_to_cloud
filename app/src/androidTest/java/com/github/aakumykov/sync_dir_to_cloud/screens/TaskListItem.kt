package com.github.aakumykov.sync_dir_to_cloud.screens

import android.view.View
import com.github.aakumykov.sync_dir_to_cloud.R
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

class TaskListItem(matcher: Matcher<View>) : KRecyclerItem<TaskListItem>(matcher) {

    val moreButton = KButton { withId(R.id.moreButton) }
    val sourcePath = KTextView { withId(R.id.sourcePath) }
    val targetPath = KTextView { withId(R.id.targetPath) }

    fun clickMoreButton() = moreButton.click()
}