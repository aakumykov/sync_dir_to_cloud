package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CacheDirTest {

    @Test
    fun cache_dir_is_dir() {
        Assert.assertTrue(cacheDir.isDirectory)
    }

    @Test
    fun cache_dir_is_writeable() {
        Assert.assertTrue(
            File(cacheDir, randomName).mkdir()
        )
    }
}