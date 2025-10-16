package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._deep_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import org.junit.Assert
import org.junit.Test

// StaticDeepDirsWithoutBackup
abstract class StaticDeepDirsWithoutBackup : StaticDeepDirsBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    @Test
    override fun deep_empty_dirs_in_source() {
        super.deep_empty_dirs_in_source()

//        Assert.assertTrue(sDeepDir.exists())
//        Assert.assertTrue(tDeepDir.exists())
//
//        Assert.assertEquals(1, fileHelper.listSourceDir().size)
//        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }


    @Test
    override fun deep_empty_dirs_in_target() {
        super.deep_empty_dirs_in_target()

        Assert.assertFalse(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertEquals(0, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

    }


    @Test
    override fun same_name_deep_empty_dirs_in_source_and_target() {
        super.same_name_deep_empty_dirs_in_source_and_target()

        Assert.assertTrue(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertTrue(sDeepDir.isEmpty)
        Assert.assertTrue(tDeepDir.isEmpty)

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

    }


    @Test
    override fun diff_name_deep_dirs_in_source_and_target() {
        super.diff_name_deep_dirs_in_source_and_target()

        Assert.assertTrue(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertTrue(sDeepDirInTarget.exists())
        Assert.assertFalse(tDeepDirInSource.exists())

        // Теоретически, часть пути может совпасть, и каталог окажется непустым,
        // но с UUID-именами эта вероятность очень низкая.
        Assert.assertTrue(sDeepDir.isEmpty)
        Assert.assertTrue(tDeepDir.isEmpty)

        Assert.assertTrue(sDeepDirInTarget.isEmpty)

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)

    }
}