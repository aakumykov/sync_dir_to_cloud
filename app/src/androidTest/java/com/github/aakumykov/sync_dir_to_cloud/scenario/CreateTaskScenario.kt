package com.github.aakumykov.sync_dir_to_cloud.scenario

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskEditScreen
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskEditScreen.sourcePathSelectionButton
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListScreen
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.text.KTextView

class CreateTaskScenario() : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Нажать кнопку Добавить") {
            TaskListScreen {
                addTaskButton.apply {
                    isVisible()
                    isClickable()
                    click()
                }
            }
        }
        step("Открыть окно выбора пути к источнику") {
            TaskEditScreen {
                sourcePathSelectionButton.click()
            }
        }
        step("Если нет локального хранилища, создать его") {
            TaskEditScreen {
                addStorageButton.apply {
                    isVisible()
//                    isClickable() // Не работает на SpeedDialView
                    click()
                }
                localStorageSubbutton.apply {
                    isVisible()
                    click()
                }
            }
        }
    }
}