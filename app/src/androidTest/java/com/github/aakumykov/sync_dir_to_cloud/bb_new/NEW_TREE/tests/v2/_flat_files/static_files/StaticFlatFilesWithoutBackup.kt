package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.static_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import org.junit.Assert
import org.junit.Test

// TODO: тесты, где бы эти методы выдавали ошибку

open class StaticFlatFilesWithoutBackup : StaticFlatFilesBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    // ==== sync ===>
    @Test
    override fun empty_file_in_source_and_no_files_in_target() {
        super.empty_file_in_source_and_no_files_in_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sFile)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sFileInTarget)
    }


    // ==== sync ===>
    @Test
    override fun no_files_in_source_and_empty_file_in_target() {
        super.no_files_in_source_and_empty_file_in_target()

        Assert.assertEquals(0, fileHelper.countSourceDirItems())

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        Assert.assertTrue(tFile.exists())
        Assert.assertTrue(tFile.isEmpty)
    }


    // ==== sync ===>
    @Test
    override fun same_name_empty_files_in_src_and_tgt() {
        super.same_name_empty_files_in_src_and_tgt()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(commonFileInSource)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(commonFileInTarget)
    }


    // ==== sync ===>
    @Test
    override fun diff_names_empty_files_in_source_and_target() {
        super.diff_names_empty_files_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sFile)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(tFile)
        assertExistsAndEmpty(sFileInTarget)
    }


    // ==== sync ===>
    @Test
    override fun data_file_in_source_and_no_files_in_target() {
        super.data_file_in_source_and_no_files_in_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    // ==== sync ===>
    @Test
    override fun no_files_in_source_and_data_file_in_target() {
        super.no_files_in_source_and_data_file_in_target()

        Assert.assertEquals(0, fileHelper.countSourceDirItems())

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndContains(tFile, tFileData)
    }


    // ==== sync ===>
    @Test
    override fun same_name_data_files_in_source_and_target() {
        super.same_name_data_files_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        Assert.assertEquals(1, fileHelper.countTargetDirItems())

        assertExistsAndContains(commonFileInSource, commonFileData)
        assertExistsAndContains(commonFileInTarget, commonFileData)
    }


    // ==== sync ===>
    @Test
    override fun diff_names_data_files_in_source_and_target() {
        super.diff_names_data_files_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndContains(tFile, tFileData)
        assertExistsAndContains(sFileInTarget, sFileData)

        Assert.assertEquals(
            fileHelper.getFileContents(sFile).joinToString(),
            fileHelper.getFileContents(sFileInTarget).joinToString()
        )
    }
}