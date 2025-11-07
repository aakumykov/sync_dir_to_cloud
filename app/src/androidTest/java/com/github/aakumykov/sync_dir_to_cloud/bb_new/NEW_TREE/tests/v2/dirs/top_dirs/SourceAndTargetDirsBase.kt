package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import org.junit.Assert

abstract class SourceAndTargetDirsBase : SyncTestBase() {

    open fun empty_source_and_target_dirs_exists() {
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }


    open fun re_creating_missing_source_dir() {
        fileHelper.deleteSourceDirRecursively()
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
        doSync()
    }


    open fun re_creating_missing_target_dir() {
        fileHelper.deleteTargetDirRecursively()
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())
        doSync()
    }
}
