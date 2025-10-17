package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.dynamic_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

class DynamicFlatFilesWithoutBackup : DynamicFlatFilesBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    @Test
    override fun empty_test() {

    }


    @Test
    override fun preparing_test() {
        super.preparing_test()
    }


    // [1] --- [1]
    // [1*] --- [1]
    // sync
    // [1*] --- [1*]
    @Test
    override fun source_file_changed() {
        super.source_file_changed()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndContains(sFile, newSourceFileData)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        Assert.assertTrue(sFileInTarget.exists())

        val sfc = fileHelper.getFileContents(sFile)
        val sftc = fileHelper.getFileContents(sFileInTarget)

        val sd = sFileData
        val nsd = newSourceFileData

        println()

        TimeUnit.SECONDS.sleep(4)

        assertExistsAndContains(sFileInTarget, newSourceFileData)
    }


    // [1] --- [1]
    // [1] --- [1*]
    // sync
    // [1] --- [1]
    @Test
    override fun target_file_changed() {
        super.target_file_changed()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndContains(sFileInTarget, sFileData)
    }
}