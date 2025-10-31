package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_has_no_extra_files

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir.cacheDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssertDeepDirHasNoExtraFilesTest {

    @Test
    fun empty_dir_tree_has_no_extra_files() {
        val deepDirName = randomDeepDirName
        val deepDirNameParts = "1/2/3/4/5".split(CloudWriter.DS)
        val deepDir = File(cacheDir, deepDirName)

        Assert.assertTrue(deepDir.mkdirs())
        Assert.assertTrue(deepDir.exists())

        assertDeepDirTreeHasNoExtraFiles(cacheDir, deepDirName)
    }
}