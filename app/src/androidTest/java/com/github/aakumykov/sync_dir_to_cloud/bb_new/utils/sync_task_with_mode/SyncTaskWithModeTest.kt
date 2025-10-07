package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.sync_task_with_mode

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalForwardSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import org.junit.Assert
import org.junit.Test

class SyncTaskWithModeTest {

    @Test
    fun sync_task_with_custom_mode_contains_this_mode() {
        listOf(SyncMode.SYNC, SyncMode.MIRROR).forEach { mode ->
            val task = syncTaskWithMode(syncMode = mode, taskConfig = LocalToLocalForwardSyncWithoutBackupTaskConfig())
            Assert.assertEquals(mode, task.syncMode)
        }
    }

    // TODO: проверять второй аргумент... но это уж слишком.
}