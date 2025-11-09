package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.task_dirs_checker.TaskDirsFixer
import org.junit.Assert

abstract class SourceAndTargetDirsBase : SyncTestBase() {

    open fun empty_source_and_target_dirs_exists_at_start() {
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }


    open fun re_creating_missing_source_dir_if_configured_yes() {
        configureReCreateDirsYes()
        deleteSourceDir()
        doSync()
    }


    open fun re_creating_missing_target_dir_if_configured_yes() {
        configureReCreateDirsYes()
        deleteTargetDir()
        doSync()
    }


    open fun re_creating_missing_source_dir_if_configured_no() {
        configureReCreateDirsNo()
        deleteSourceDir()
        doSync()
    }

    open fun re_creating_missing_target_dir_if_configured_no() {
        configureReCreateDirsNo()
        deleteTargetDir()
        doSync()
    }


    private fun deleteSourceDir() {
        fileHelper.deleteSourceDirRecursively()
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
    }

    private fun deleteTargetDir() {
        fileHelper.deleteTargetDirRecursively()
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())
    }

    private fun configureReCreateDirsYes() {
        appComponent.getAppSettings().apply {
            restoreLostSourceAndTargetDirs = true
            Assert.assertTrue(restoreLostSourceAndTargetDirs)
        }
    }

    private fun configureReCreateDirsNo() {
        appComponent.getAppSettings().apply {
            restoreLostSourceAndTargetDirs = false
            Assert.assertFalse(restoreLostSourceAndTargetDirs)
        }
    }
}
