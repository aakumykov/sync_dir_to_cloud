package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.common.TestDbStuff
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateAndCheckLocalTask
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesCreator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskCreationTest() : StorageAccessTestCase() {

    private val testFilesCreator by lazy { TestFilesCreator(TestTaskConfig) }

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {
        scenario(
            CreateAndCheckLocalTask(
                syncMode = SyncMode.SYNC,
                dbStuff = TestDbStuff.get(device.targetContext)
            )
        )

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
                testFilesCreator.createFileInSource(FILE_1_NAME, FILE_1_SIZE).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(FILE_1_SIZE, it.length().toInt())
                }
            }
        }

        step("Запуск задачи") {
            runTest {
                syncTaskExecutor(this).executeSyncTask(TestTaskConfig.TASK_ID)
            }
        }

        step("Проверка, что синхронизация одного файла прошла корректно") {
            run {
                testFilesCreator.fileInTarget(FILE_1_NAME).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(it.length().toInt(),FILE_1_SIZE)
                }
            }
        }
    }


    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return appComponent.getSyncTaskExecutorAssistedFactory().create(scope)
    }

    companion object {
        const val FILE_1_NAME = "file1.txt"
        const val FILE_1_SIZE = 1024
    }
}