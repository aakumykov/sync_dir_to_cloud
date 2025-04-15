package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.TestDaoSet
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.TARGET_DIR
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.TestFileManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class DirsCreationTest() : StorageAccessTestCase() {

    private val testFileManager by lazy { TestFileManager(LocalTaskConfig, LocalFileCofnig) }
    private val daoSet: DaoSet get() = TestDaoSet.get(device.targetContext)
    private val syncTaskDAO get() = daoSet.syncTaskDAO


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