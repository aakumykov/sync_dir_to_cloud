package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TargetDirCreationDeletionTest() : StorageAccessTestCase() {

    private val localTaskConfig = LocalTaskConfig()
    private val localFileHelper by lazy { LocalFileHelper(localTaskConfig, LocalFileCofnig) }
    private val syncTaskDAO get() = TestComponentHolder.testSyncTaskDAO

    @Test
    fun when_recreate_target_dir_when_it_recreated() = run {

        // Вариант 1: целевой каталог существует
        step("Удаление целевого каталога ('${localTaskConfig.TARGET_PATH}'), если он есть") {
            runTest {
                if (localTaskConfig.TARGET_DIR.exists()) {
                    localTaskConfig.TARGET_DIR.deleteRecursively()
                    Assert.assertFalse(localTaskConfig.TARGET_DIR.exists())
                }
            }
        }

        // Вариант 2: целевой каталог не существует
        step("Создание целевого каталога ('${localTaskConfig.TARGET_PATH}')") {
            runTest {
                localTaskConfig.TARGET_DIR.mkdir()
                Assert.assertTrue(localTaskConfig.TARGET_DIR.exists())
            }
        }
    }


}