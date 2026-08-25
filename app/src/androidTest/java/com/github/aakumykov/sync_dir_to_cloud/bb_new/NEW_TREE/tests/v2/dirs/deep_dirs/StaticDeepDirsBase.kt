package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.deep_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class StaticDeepDirsBase : SyncTestBase() {

    /**
     * Просто "существование" каталогов.
     *
     * Глубокие пустые каталоги - пусто [deep_empty_dir_in_source]
     * Пусто - глубокие пустые каталоги [deep_empty_dir_in_target]
     *
     * Одноимённыя глубокия каталоги в источнике и приёмнике [same_name_deep_empty_dirs_in_source_and_target]
     * Разноимённыя глубокия каталоги в источнике и приёмнике [diff_name_deep_dirs_in_source_and_target]
     */


    // ====== sync =====>
    open fun deep_empty_dir_in_source() {
        fileHelper.createDirInSource(commonDeepDirName)
        doSync()
    }


    // ====== sync =====>
    open fun deep_empty_dir_in_target() {
        fileHelper.createDirInTarget(commonDeepDirName)
        doSync()
    }


    // ====== sync =====>
    open fun same_name_deep_empty_dirs_in_source_and_target() {
        fileHelper.createDirInSource(commonDeepDirName)
        fileHelper.createDirInTarget(commonDeepDirName)
        doSync()
    }


    // ====== sync =====>
    open fun diff_name_deep_dirs_in_source_and_target() {
        fileHelper.createDirInSource(sDeepDirName(2))
        fileHelper.createDirInTarget(tDeepDirName(2))
        doSync()
    }
}