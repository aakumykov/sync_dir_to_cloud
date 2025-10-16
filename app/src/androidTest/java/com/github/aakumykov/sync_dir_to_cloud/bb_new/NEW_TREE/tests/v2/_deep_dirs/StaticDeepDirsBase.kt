package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._deep_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import org.junit.Assert
import org.junit.Test

abstract class StaticDeepDirsBase : SyncTestBase() {

    /**
     * Просто "существование" каталогов.
     *
     * Глубокие пустые каталоги - пусто [deep_empty_dirs_in_source]
     * Пусто - глубокие пустые каталоги [deep_empty_dirs_in_target]
     *
     * Одноимённыя глубокия каталоги в источнике и приёмнике [same_name_deep_empty_dirs_in_source_and_target]
     * Разноимённыя глубокия каталоги в источнике и приёмнике [diff_name_deep_dirs_in_source_and_target]
     */


    // ====== sync =====>
    open fun deep_empty_dirs_in_source() {
        fileHelper.createDirInSource(commonDeepDirName)
        val sList = taskConfig.SOURCE_DIR.list()
        doSync()
    }


    // ====== sync =====>
    open fun deep_empty_dirs_in_target() {
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
        fileHelper.createDirInSource(sDeepDirName)
        fileHelper.createDirInTarget(tDeepDirName)
        doSync()
    }
}