package com.github.aakumykov.sync_dir_to_cloud.screens

import android.view.View
import com.github.aakumykov.sync_dir_to_cloud.R
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

class TaskListItemScreen(matcher: Matcher<View>): KRecyclerItem<TaskListItemScreen>(matcher) {
    val itemView = KView(matcher) { withId(R.id.taskListItemView) }
    val sourcePath = KTextView(matcher) { withId(R.id.sourcePath) }
    val targetPath = KTextView(matcher) { withId(R.id.targetPath) }
    val runButton = KButton(matcher) { withId(R.id.runButton) }
}