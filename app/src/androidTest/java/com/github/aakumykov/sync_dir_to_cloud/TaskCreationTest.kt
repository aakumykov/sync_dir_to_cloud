package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.common.TestDbStuff
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateAndCheckLocalTask
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateSourceFile
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateTargetFile
import com.github.aakumykov.sync_dir_to_cloud.scenario.DeleteSourceFile
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskCreationTest() : StorageAccessTestCase() {

    private val testFilesManager by lazy { TestFilesManager(TestTaskConfig) }

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

        scenario(
            CreateSourceFile(FILE_1_NAME, FILE_1_SIZE)
        )

        runSync()

        step("Проверка, что синхронизация одного файла прошла корректно") {
            run {
                testFilesManager.fileInTarget(FILE_1_NAME).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(it.length().toInt(),FILE_1_SIZE)
                }
            }
        }


        scenario(
            CreateTargetFile(FILE_1_NAME, FILE_1_SIZE_MOD)
        )

        runSync()

        step("Проверка, что файл в приёмнике заменён файлом из источника") {
            runTest {
                val sourceFile = testFilesManager.fileInSource(FILE_1_NAME)
                val targetFile = testFilesManager.fileInTarget(FILE_1_NAME)
                Assert.assertTrue(sourceFile.exists())
                Assert.assertTrue(targetFile.exists())
                Assert.assertEquals(sourceFile.length(), targetFile.length())
            }
        }

        step("Удаление файла в приёмнике, удалённого в источнике") {
            scenario(
                CreateSourceFile(FILE_1_NAME, FILE_1_SIZE)
            )
            runSync()
            
            scenario(
                DeleteSourceFile(FILE_1_NAME)
            )
            runSync()

            Assert.assertFalse(testFilesManager.fileInTarget(FILE_1_NAME).exists())
        }
    }


    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return appComponent.getSyncTaskExecutorAssistedFactory().create(scope)
    }

    private fun runSync() = run {
        step("Запуск задачи") {
            runTest {
                syncTaskExecutor(this).executeSyncTask(TestTaskConfig.TASK_ID)
            }
        }
    }

    companion object {
        const val FILE_1_NAME = "file1.txt"
        const val FILE_1_SIZE = 1024
        const val FILE_1_SIZE_MOD = 544
    }
}