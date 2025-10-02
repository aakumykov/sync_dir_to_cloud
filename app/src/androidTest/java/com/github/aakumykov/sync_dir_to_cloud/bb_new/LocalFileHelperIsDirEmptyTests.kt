package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class LocalFileHelperIsDirEmptyTests : LocalFileHelperTestBase() {

    @Before
    fun removeSourceAndTargetDirs() = run {
        deleteSourceAndTargetDirs()
    }

    /**
     * Тест метода [LocalFileHelper.isSourceDirEmpty]
     */
    @Test
    fun is_source_dir_empty_after_creation() = run {
        create_dir_with_check_after(taskConfig.SOURCE_DIR) {
            fileHelper.isSourceDirEmpty()
        }
    }

    /**
     * Тест метода [LocalFileHelper.isTargetDirEmpty]
     */
    @Test
    fun is_target_dir_empty_after_creation() = run {
        create_dir_with_check_after(taskConfig.TARGET_DIR) {
            fileHelper.isTargetDirEmpty()
        }
    }


    private fun create_dir_with_check_after(dir: File, action: () -> Boolean) = run {

        val dirPath = dir.absolutePath

        step("Создаю каталог '$dirPath'") {
            Assert.assertTrue(dir.mkdir())
        }

        step("Проверяю, что каталог '$dirPath' начал существовать") {
            Assert.assertTrue(dir.exists())
        }

        step("Проверяю, что каталог '$dirPath' пустой") {
            Assert.assertTrue(action.invoke())
        }
    }
}