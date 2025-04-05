package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.parent_child.TestDbStuff
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateAndCheckLocalTask
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskCreationTest2() : StorageAccessTestCase() {

    private val dbStuff = TestDbStuff.get(device.targetContext)
    private val testFilesCreator by lazy { TestFilesCreator2(dbStuff) }

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {
        scenario(
            CreateAndCheckLocalTask(
                syncMode = SyncMode.SYNC,
                dbStuff = dbStuff
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
                val fileSizeKb = 10
                testFilesCreator.createFileInSource("file1.txt", fileSizeKb).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(fileSizeKb, it.length().toInt())
                }
            }
        }

        step("Запуск задачи") {
            runTest {
                syncTaskExecutor(this).executeSyncTask(TestTaskConfig.ID)
            }
        }
    }


    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return appComponent.getSyncTaskExecutorAssistedFactory().create(scope)
    }
}