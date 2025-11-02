package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_is_empty_as_all_levels

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir.cacheDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssertDeepDirIsEmptyAtAllLevelsTest {

    private val newEmptyBaseDir: File
        get() = File(cacheDir, randomName).apply {
            Assert.assertTrue(mkdir())
        }

    @Test
    fun empty_dir_name() {
        assertDeepDirIsEmptyAtAllLevels(newEmptyBaseDir, "")
    }

    @Test
    fun empty_deep_dir_is_empty_at_all_levels() {
        val baseDir = newEmptyBaseDir
        val deepDirName = randomDeepDirName
        prepareDeepDir(baseDir, deepDirName)
        assertDeepDirIsEmptyAtAllLevels(baseDir, deepDirName)
    }

    @Test
    fun deep_dir_with_tail_file_throws_exception() {
        val emptyBaseDir = newEmptyBaseDir
        val deepDirName = randomDeepDirName
        Assert.assertThrows(AssertionError::class.java) {
            prepareDeepDir(emptyBaseDir, deepDirName).also { deepDir ->
                Assert.assertTrue(File(deepDir, randomName).createNewFile())
                assertDeepDirIsEmptyAtAllLevels(emptyBaseDir, deepDirName)
            }
        }
    }

    @Test
    fun deep_dir_with_branch_throws_exception() {
        val emptyBaseDir = newEmptyBaseDir
        Assert.assertThrows(AssertionError::class.java) {
            val deepDirFirstPart = randomDeepDirName(1,3)

            val deepDirName = deepDirFirstPart + CloudWriter.DS + randomDeepDirName(1,3)
            val branchDirName = deepDirFirstPart + CloudWriter.DS + randomName

            prepareDeepDir(emptyBaseDir, deepDirName)
            prepareDeepDir(emptyBaseDir, branchDirName)

            assertDeepDirIsEmptyAtAllLevels(emptyBaseDir, deepDirName)
        }
    }

    private fun prepareDeepDir(baseDir: File, deepDirName: String): File {
        return File(baseDir, deepDirName).let { dir ->
            Assert.assertTrue(dir.mkdirs())
            Assert.assertTrue(dir.exists())
            dir
        }
    }
}