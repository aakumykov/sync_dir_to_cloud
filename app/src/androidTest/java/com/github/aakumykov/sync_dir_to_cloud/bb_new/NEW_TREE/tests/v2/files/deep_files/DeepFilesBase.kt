package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class DeepFilesBase : SyncTestBase() {

    protected fun prepareSourceDeepFileAndSyncItWithTarget() {
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, sFileData)

        doSync()

        assertSourceDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, sFileData)

        assertTargetDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, sFileData)
    }
}