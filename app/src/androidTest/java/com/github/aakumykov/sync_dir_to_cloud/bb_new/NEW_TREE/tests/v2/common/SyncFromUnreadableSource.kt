package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.syncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.unreadableSourceNoBackupTaskConfig
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncFromUnreadableSource : SyncTestBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = unreadableSourceNoBackupTaskConfig

    /**
     * [com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader]
     * при чтении нечитаемого каталога первым делом добавляет в список его самого
     * и не бросает исключение...
     */
    @Test
    fun sync_from_unreadable_source_throws_exception() {
        Assert.assertThrows(Exception::class.java) {
            runBlocking {
                syncTaskExecutor(this).executeSyncTask(taskConfig.TASK_ID)
            }
        }
    }
}