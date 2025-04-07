package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.common.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.common.TestDaoSet
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateLocalTask
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskAndDirCreationTests() : StorageAccessTestCase() {

    private val testFilesManager by lazy { TestFilesManager(TestTaskConfig) }
    private val daoSet: DaoSet get() = TestDaoSet.get(device.targetContext)
    private val syncTaskDAO get() = daoSet.syncTaskDAO
    private val cloudAuthDAO get() = daoSet.cloudAuthDAO

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {

        val syncMode = SyncMode.SYNC

        scenario(
            CreateLocalTask(syncMode, daoSet)
        )

        step("Проверка, что CloudAuth создался") {
            runTest {
                val cloudAuth = cloudAuthDAO.get(TestTaskConfig.AUTH_ID)

                Assert.assertEquals(cloudAuth.id, TestTaskConfig.AUTH_ID)
                Assert.assertEquals(cloudAuth.name, TestTaskConfig.AUTH_NAME)
                Assert.assertEquals(cloudAuth.authToken, TestTaskConfig.AUTH_TOKEN)
                Assert.assertEquals(cloudAuth.storageType, TestTaskConfig.STORAGE_TYPE)
            }
        }

        step("Проверка, что SyncTask создался") {
            runTest {
                val syncTask = syncTaskDAO.get(TestTaskConfig.TASK_ID)

                Assert.assertEquals(syncTask.id, TestTaskConfig.TASK_ID)
                Assert.assertEquals(syncTask.syncMode, syncMode)
                Assert.assertEquals(syncTask.sourcePath, TestTaskConfig.SOURCE_PATH)
                Assert.assertEquals(syncTask.targetPath, TestTaskConfig.TARGET_PATH)
                Assert.assertEquals(syncTask.sourceStorageType, TestTaskConfig.STORAGE_TYPE)
                Assert.assertEquals(syncTask.targetStorageType, TestTaskConfig.STORAGE_TYPE)
                Assert.assertEquals(syncTask.intervalHours, TestTaskConfig.INTERVAL_HOURS)
                Assert.assertEquals(syncTask.intervalMinutes, TestTaskConfig.INTERVAL_MINUTES)
                Assert.assertEquals(syncTask.sourceAuthId, TestTaskConfig.AUTH_ID)
                Assert.assertEquals(syncTask.targetAuthId, TestTaskConfig.AUTH_ID)

            }
        }

        /*scenario(
            ReCreateTargetDir()
        )

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
        }*/
    }


    @Test
    fun when_recreate_target_dir_when_it_recreated() = run {
        val targetDir = File(TestTaskConfig.TARGET_PATH)
        // Вариант 1: целевой каталог существует
        step("Удаление целевого каталога ('${TestTaskConfig.TARGET_PATH}'), если он есть") {
            runTest {
                if (targetDir.exists()) {
                    targetDir.deleteRecursively()
                    Assert.assertFalse(targetDir.exists())
                }
            }
        }
        // Вариант 2: целевой каталог не существует
        step("Создание целевого каталога ('${TestTaskConfig.TARGET_PATH}')") {
            runTest {
                targetDir.mkdir()
                Assert.assertTrue(targetDir.exists())
            }
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