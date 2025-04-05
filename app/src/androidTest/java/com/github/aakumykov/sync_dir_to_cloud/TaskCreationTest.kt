package com.github.aakumykov.sync_dir_to_cloud

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListItemScreen
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListScreen
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.test_utils.TestFilesCreator
import com.github.aakumykov.sync_dir_to_cloud.test_utils.TestTaskCreator
import com.github.aakumykov.sync_dir_to_cloud.view.MainActivity
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Description
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class TaskCreationTest : StorageAccessTestCase() {
    private val taskId: String = TestTaskCreator.TEST_ID

    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return appComponent
            .getSyncTaskExecutorAssistedFactory()
            .create(scope)
    }

    private val testFilesCreator: TestFilesCreator by lazy {
        appComponent.getTestFilesCreator()
    }


    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()


    @After
    fun delayAfterTest() = TimeUnit.SECONDS.sleep(2)


    @Test
    fun newTaskCanBeCreated() = run {

        step("Создать тестовую задачу типа 'SYNC'") {
            TaskListScreen {
                createMirrorTestTaskButton.apply {
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

        step("Подготовка тестовых файлов в Источнике") {
            runBlocking {
                testFilesCreator.createFileInSource(taskId)
            }
        }

        // TODO: как проверять, что задача запустилась:
        //  через БД или графический интерфейс?
        step("Запуск задачи") {
            /*TaskListScreen {
                recyclerView {
                    firstChild<TaskListItemScreen> {
                        runButton {
                            isVisible()
                            isClickable()
                            click()
                        }
                    }
                }
            }*/
            runTest {
                syncTaskExecutor(this).executeSyncTask(TestTaskCreator.TEST_ID)
            }
        }

        step("Подготовка тестовых файлов в Приёмнике") {
            runBlocking {
                testFilesCreator.createFileInTarget(taskId)
            }
        }

        // TODO: как проверять, что задача запустилась:
        //  через БД или графический интерфейс?
        step("Запуск задачи") {
            /*TaskListScreen {
                recyclerView {
                    firstChild<TaskListItemScreen> {
                        runButton {
                            isVisible()
                            isClickable()
                            click()
                        }
                    }
                }
            }*/
            runTest {
                syncTaskExecutor(this).executeSyncTask(TestTaskCreator.TEST_ID)
            }
        }

        step("Проверка результатов запуска") {

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