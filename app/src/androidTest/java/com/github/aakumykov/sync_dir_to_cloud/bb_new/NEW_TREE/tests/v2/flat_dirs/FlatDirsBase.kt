package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName

abstract class FlatDirsBase : SyncTestBase() {

    protected val commonDirName = randomName

    protected val sDirName = randomName
    protected val tDirName = randomName

    protected val sCommonDir = fileHelper.dirInSource(commonDirName)
    protected val tCommonDir = fileHelper.dirInTarget(commonDirName)

    protected val sDir = fileHelper.dirInSource(sDirName)
    protected val tDir = fileHelper.dirInTarget(tDirName)

    protected val sDirInTarget = fileHelper.dirInTarget(sDirName)
    protected val tDirInSource = fileHelper.dirInSource(tDirName)
}