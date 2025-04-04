package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateLocalTask
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.test_utils.TestFilesCreator2
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskCreationTest2() : TestCase() {

    private val testFilesCreator by lazy { TestFilesCreator2(currentTask) }
    private val syncTaskReader by lazy { appComponent.getSyncTaskReader() }
    private var _currentTask: SyncTask? = null
    private val currentTask: SyncTask get() = _currentTask!!

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {
        scenario(
            CreateLocalTask(SyncMode.SYNC, device.targetContext)
        )

        step("Считывание созданной тестовой задачи") {
            runTest {
                _currentTask = syncTaskReader.getSyncTask(TestTaskConfig.ID)
                Assert.assertTrue(_currentTask != null)
            }
        }

        step("Удаление старого и создание нового каталога приёмника") {
            File(TestTaskConfig.TARGET_PATH).also { targetDir: File ->
                targetDir.deleteRecursively()
                Assert.assertFalse(targetDir.exists())

                targetDir.mkdir()
                Assert.assertTrue(targetDir.exists())
            }
        }

        step("Создание тестового файла в источнике") {
            runTest {
                val fileSizeKb = 10
                testFilesCreator.createFileInSource("file1.txt", fileSizeKb).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(fileSizeKb, it.length())
                }
            }
        }

        /*step("Запуск задачи") {
            runTest {
                syncTaskExecutor(this).executeSyncTask(ID)
            }
        }*/
    }


    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return appComponent.getSyncTaskExecutorAssistedFactory().create(scope)
    }
}