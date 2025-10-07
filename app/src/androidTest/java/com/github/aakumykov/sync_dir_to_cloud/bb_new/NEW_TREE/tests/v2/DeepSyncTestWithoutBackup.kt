package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import org.junit.Test

class DeepSyncTestWithoutBackup : SyncTestBase() {

    /**
     * Глубокие пустые каталоги - пусто [deep_empty_dirs_in_source]
     * Пусто - глубокие пустые каталоги [deep_empty_dirs_in_target]
     */

    @Test
    fun deep_empty_dirs_in_source() {
        val dirName = randomDeepDirName
    }
}