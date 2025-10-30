package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.flat_dirs.empty_endpoints_MOVE

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class EmptyBase : SyncTestBase() {

    open fun empty_source_and_target_sync() {
        doSync()
    }
}