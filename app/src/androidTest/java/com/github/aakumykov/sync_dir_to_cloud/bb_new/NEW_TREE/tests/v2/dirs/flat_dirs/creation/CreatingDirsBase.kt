package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.flat_dirs.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class CreatingDirsBase : SyncTestBase() {

    // ==== sync ===>
    open fun only_in_source_dir() {
        fileHelper.createDirInSource(sDirName)
        doSync()
    }


    // ==== sync ===>
    open fun only_in_target_dir() {
        fileHelper.createDirInTarget(tDirName)
        doSync()
    }


    // ==== sync ===>
    open fun same_name_dirs_in_source_and_target() {
        fileHelper.createDirInSource(commonDirName)
        fileHelper.createDirInTarget(commonDirName)
        doSync()
    }


    // ==== sync ===>
    open fun diff_name_dirs_in_source_and_target() {
        fileHelper.createDirInSource(sDirName)
        fileHelper.createDirInTarget(tDirName)
        doSync()
    }
}