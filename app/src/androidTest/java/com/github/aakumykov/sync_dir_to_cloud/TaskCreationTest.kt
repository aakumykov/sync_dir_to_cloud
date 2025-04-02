package com.github.aakumykov.sync_dir_to_cloud

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListItemScreen
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListScreen
import com.github.aakumykov.sync_dir_to_cloud.view.MainActivity
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.hamcrest.Description
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

class TaskCreationTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple( // simple/advanced - it doesn't matter
        customize = {
            // storage support for Android API 30+
            if (isAndroidRuntime) {
                UiDevice
                    .getInstance(instrumentation)
                    .executeShellCommand("appops set --uid ${InstrumentationRegistry.getInstrumentation().targetContext.packageName} MANAGE_EXTERNAL_STORAGE allow")
            }
        }
    )
) {
    // storage support for Android API 29-
    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )


    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()


    @After
    fun delayAfterTest() = TimeUnit.SECONDS.sleep(2)


    @Test
    fun newTaskCanBeCreated() = run {

        step("Нажать кнопку Добавить") {
            TaskListScreen {
                createTestTaskButton.apply {
                    isVisible()
                    isClickable()
                    click()
                }
            }
        }

        step("Проверка, что задача создалась") {
            TaskListScreen {
                recyclerView {
                    /*firstChild<TaskListItemScreen> {
                        isVisible()
                    }*/
                    childWith<TaskListItemScreen> {
                        // FIXME: должна быть ошибка, но её нет
                        onData(withTaskId(TestTaskCreator.TEST_ID+"@")).apply {
                            isDisplayed()
                        }
                    }
                }
            }
        }

        step("Удаление старой целевой папки") {
            targetDir.apply {
                Assert.assertTrue(
                    if (exists()) deleteRecursively()
                    else true
                )
            }
        }

        step("Создание целевой папки") {
            targetDir.also {
                Assert.assertTrue(
                    it.exists() || it.mkdirs()
                )
            }
        }

        step("Запуск задачи") {
            TaskListScreen {
                recyclerView {
                    firstChild<TaskListItemScreen> {
                        runButton {
                            isVisible()
                            isClickable()
                            click()
                        }
                    }
                }
            }
        }


    }

    private val targetDir: File
        get() = File(TestTaskCreator.testTaskLocalTargetPath)

    companion object {
        fun withTaskId(taskId: String) = SyncTaskMatcher(taskId)
    }
}

class SyncTaskMatcher(
    private val taskId: String,
    /*taskName: String, sourcePath: String, targetPath: String*/
) : BoundedMatcher<Any?, SyncTask>(SyncTask::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("SyncTask with id: $taskId")
    }

    override fun matchesSafely(item: SyncTask?): Boolean {
        return item?.let {
            it.id == taskId
        } ?: false
    }
}