package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.adding_more_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test

open class AddingMoreDirsWithoutBackup : AddingMoreDirsBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    // Пустой тест для проверки
    @Test
    override fun preparation_method_test() {
        super.preparation_method_test()
    }


    // ==== sync ===>
    @Test
    override fun create_additional_dir_in_source() {
        super.create_additional_dir_in_source()

        Assert.assertEquals(2, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(newSDir)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
        assertExistsAndEmpty(newSDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun create_additional_dir_in_target() {
        super.create_additional_dir_in_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
        assertExistsAndEmpty(newTDir)
    }


    // ==== sync ===>
    @Test
    override fun create_diff_names_additional_dirs_in_source_and_target() {
        super.create_diff_names_additional_dirs_in_source_and_target()

        Assert.assertEquals(2, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(newSDir)

        Assert.assertEquals(3, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
        assertExistsAndEmpty(newTDir)
        assertExistsAndEmpty(newSDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun create_same_name_additional_dirs_in_source_and_target() {
        super.create_same_name_additional_dirs_in_source_and_target()

        Assert.assertEquals(2, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(newCommonSDir)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
        assertExistsAndEmpty(newCommonTDir)
    }
}