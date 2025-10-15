package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.adding_new_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test

class AddingNewDirsWithoutBackup : AddingNewDirsBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    // ==== sync ===>
    @Test
    override fun create_additional_dir_in_source() {
        super.create_additional_dir_in_source()

        Assert.assertEquals(2, fileHelper.createSourceDir())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(newSDir)

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
        assertExistsAndEmpty(newSDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun create_additional_dir_in_target() {
        super.create_additional_dir_in_target()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
        assertExistsAndEmpty(newTDir)
        assertExistsAndEmpty(sDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun create_diff_names_additional_dirs_in_source_and_target() {
        super.create_diff_names_additional_dirs_in_source_and_target()

        Assert.assertEquals(2, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(newSDir)

        Assert.assertEquals(3, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
        assertExistsAndEmpty(newTDir)
        assertExistsAndEmpty(newSDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun create_same_name_additional_dirs_in_source_and_target() {
        super.create_same_name_additional_dirs_in_source_and_target()

        Assert.assertEquals(2, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(sNewCommonDir)

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
        assertExistsAndEmpty(tNewCommonDir)
    }
}