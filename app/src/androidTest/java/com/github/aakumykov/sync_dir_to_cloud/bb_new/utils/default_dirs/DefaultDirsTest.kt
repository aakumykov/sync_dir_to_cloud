package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class DefaultDirsTest : StorageAccessTestCase() {

    @Before
    fun clear_downloads_dir() {

    }

    @Test
    fun default_local_source_dir_exists_readable_and_writable() {
        checkDirCanBeCreatedReadableWritable(defaultLocalSourceDir)
    }

    @Test
    fun default_local_target_dir_exists_readable_and_writable() {
        checkDirCanBeCreatedReadableWritable(defaultLocalTargetDir)
    }

    private fun checkDirCanBeCreatedReadableWritable(dir: File) {
        Assert.assertTrue(dir.deleteRecursively())
        Assert.assertTrue(dir.mkdir())
        Assert.assertTrue(null != dir.list())
        Assert.assertTrue(File(dir, randomName).mkdir())
    }
}