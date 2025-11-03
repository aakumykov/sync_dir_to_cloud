package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.syncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.unreadableSourceNoBackupTaskConfig
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncFromUnreadableSource : SyncTestBase() {

    override val taskConfig: TaskConfig
        get() = unreadableSourceNoBackupTaskConfig

    @Test
    fun sync_from_unreadable_source_throws_exception() = runBlocking {
        syncTaskExecutor(this).executeSyncTask(taskConfig.TASK_ID)
    }
}