package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.TestDaoSet
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.task.CreateLocalTask
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.TestFilesManager
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskAndDirCreationTests() : StorageAccessTestCase() {

    private val testFilesManager by lazy { TestFilesManager(LocalTaskConfig, LocalFileCofnig) }
    private val daoSet: DaoSet get() = TestDaoSet.get(device.targetContext)
    private val syncTaskDAO get() = daoSet.syncTaskDAO
    private val cloudAuthDAO get() = daoSet.cloudAuthDAO

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {

        val syncMode = SyncMode.SYNC

        scenario(
            CreateLocalTask(LocalTaskConfig, daoSet)
        )

        step("Проверка, что CloudAuth создался") {
            runTest {
                val cloudAuth = cloudAuthDAO.get(LocalTaskConfig.AUTH_ID)

                Assert.assertEquals(cloudAuth.id, LocalTaskConfig.AUTH_ID)
                Assert.assertEquals(cloudAuth.name, LocalTaskConfig.AUTH_NAME)
                Assert.assertEquals(cloudAuth.authToken, LocalTaskConfig.AUTH_TOKEN)
                Assert.assertEquals(cloudAuth.storageType, LocalTaskConfig.STORAGE_TYPE)
            }
        }

        step("Проверка, что SyncTask создался") {
            runTest {
                val syncTask = syncTaskDAO.get(LocalTaskConfig.TASK_ID)

                Assert.assertEquals(syncTask.id, LocalTaskConfig.TASK_ID)
                Assert.assertEquals(syncTask.syncMode, syncMode)
                Assert.assertEquals(syncTask.sourcePath, LocalTaskConfig.SOURCE_PATH)
                Assert.assertEquals(syncTask.targetPath, LocalTaskConfig.TARGET_PATH)
                Assert.assertEquals(syncTask.sourceStorageType, LocalTaskConfig.STORAGE_TYPE)
                Assert.assertEquals(syncTask.targetStorageType, LocalTaskConfig.STORAGE_TYPE)
                Assert.assertEquals(syncTask.intervalHours, LocalTaskConfig.INTERVAL_HOURS)
                Assert.assertEquals(syncTask.intervalMinutes, LocalTaskConfig.INTERVAL_MINUTES)
                Assert.assertEquals(syncTask.sourceAuthId, LocalTaskConfig.AUTH_ID)
                Assert.assertEquals(syncTask.targetAuthId, LocalTaskConfig.AUTH_ID)

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
        val targetDir = File(LocalTaskConfig.TARGET_PATH)
        // Вариант 1: целевой каталог существует
        step("Удаление целевого каталога ('${LocalTaskConfig.TARGET_PATH}'), если он есть") {
            runTest {
                if (targetDir.exists()) {
                    targetDir.deleteRecursively()
                    Assert.assertFalse(targetDir.exists())
                }
            }
        }
        // Вариант 2: целевой каталог не существует
        step("Создание целевого каталога ('${LocalTaskConfig.TARGET_PATH}')") {
            runTest {
                targetDir.mkdir()
                Assert.assertTrue(targetDir.exists())
            }
        }
    }
}