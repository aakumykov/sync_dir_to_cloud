package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.TARGET_DIR
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TargetDirCreationDeletionTest() : StorageAccessTestCase() {

    private val localFileHelper by lazy { LocalFileHelper(LocalTaskConfig, LocalFileCofnig) }
    private val syncTaskDAO get() = TestComponentHolder.testSyncTaskDAO

    @Test
    fun when_recreate_target_dir_when_it_recreated() = run {

        // Вариант 1: целевой каталог существует
        step("Удаление целевого каталога ('${LocalTaskConfig.TARGET_PATH}'), если он есть") {
            runTest {
                if (TARGET_DIR.exists()) {
                    TARGET_DIR.deleteRecursively()
                    Assert.assertFalse(TARGET_DIR.exists())
                }
            }
        }

        // Вариант 2: целевой каталог не существует
        step("Создание целевого каталога ('${LocalTaskConfig.TARGET_PATH}')") {
            runTest {
                TARGET_DIR.mkdir()
                Assert.assertTrue(TARGET_DIR.exists())
            }
        }
    }


}