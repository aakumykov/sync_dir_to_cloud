package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_is_empty_as_all_levels

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir.cacheDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.file.Files.exists

@RunWith(AndroidJUnit4::class)
class AssertDeepDirIsEmptyAtAllLevelsTest {

    private val baseEmptyDir: File
        get() = File(cacheDir, randomName).apply {
            Assert.assertTrue(mkdir())
        }

    @Test
    fun empty_dir_name() {
        assertDeepDirIsEmptyAtAllLevels(baseEmptyDir, "")
    }

    @Test
    fun empty_deep_dir_is_empty_at_all_levels() {
        val deepDirName = randomDeepDirName.apply {
            File(baseEmptyDir, this).also { dir ->
                Assert.assertTrue(dir.mkdirs())
                Assert.assertTrue(dir.exists())
            }
        }
        assertDeepDirIsEmptyAtAllLevels(baseEmptyDir, deepDirName)
    }
}