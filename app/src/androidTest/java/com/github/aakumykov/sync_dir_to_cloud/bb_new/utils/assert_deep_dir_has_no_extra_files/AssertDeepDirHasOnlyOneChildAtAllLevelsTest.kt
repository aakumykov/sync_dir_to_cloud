package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_has_no_extra_files

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir.cacheDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class AssertDeepDirHasOnlyOneChildAtAllLevelsTest {

    @Test
    fun dir_with_one_level_depth() {
        val oneLevelDirName = randomName
        File(cacheDir, oneLevelDirName).apply {
            Assert.assertTrue(this.mkdir())
            Assert.assertTrue(File(this, randomName).createNewFile())
        }
        assertDeepDirHasOnlyOneChildAtAllLevels(cacheDir, oneLevelDirName)
    }

    @Test
    fun dir_tree_with_tail_file() {
        val deepDirName = randomDeepDirName
        val deepDir = prepareDeepDir(cacheDir, deepDirName)
        createFileInDir(deepDir, randomName)
        assertDeepDirHasOnlyOneChildAtAllLevels(cacheDir, deepDirName)
    }

    private fun createFileInDir(parentDir: File, fileName: String) {
        Assert.assertTrue(File(parentDir, fileName).createNewFile() )
    }

    private fun prepareDeepDir(parentDir: File, deepDirName: String): File {
        return File(parentDir, deepDirName).apply {
            Assert.assertTrue(this.mkdirs())
            Assert.assertTrue(this.exists())
        }
    }

    @Test
    fun dir_tree_with_many_tail_files_throws_exception() {
//        Assert.assertThrows(Exception::class.java) {
            val deepDirName = randomDeepDirName
            val deepDir = prepareDeepDir(cacheDir, deepDirName)
            repeat(Random.nextInt(2,11)) {
                createFileInDir(deepDir, randomName)
            }
            assertDeepDirHasOnlyOneChildAtAllLevels(cacheDir, deepDirName)
//        }
    }

    @Test
    fun dir_tree_with_branch_throws_exception() {
//        Assert.assertThrows(Exception::class.java) {

            fun randomInt_2_5(): Int = Random.nextInt(2,6)

            // Создаю первую часть глубокого каталога.
            val deepDirName1 = randomDeepDirName(randomInt_2_5())
            val deepDir1 = prepareDeepDir(cacheDir, deepDirName1)

            // Создаю доп. файл посередине глубины.
            createFileInDir(deepDir1, randomName)

            // Создаю вторую часть глубокого каталога.
            val deepDirName2 = randomDeepDirName(randomInt_2_5())
            prepareDeepDir(deepDir1, deepDirName2)

            val fullDeepDirName = deepDirName1 + CloudWriter.DS + deepDirName2

            assertDeepDirHasOnlyOneChildAtAllLevels(cacheDir, fullDeepDirName)
//        }
    }
}