package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class SourceAndTargetDirsBase : SyncTestBase() {

    open fun empty_source_and_target_dirs_exists() {
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }
}
