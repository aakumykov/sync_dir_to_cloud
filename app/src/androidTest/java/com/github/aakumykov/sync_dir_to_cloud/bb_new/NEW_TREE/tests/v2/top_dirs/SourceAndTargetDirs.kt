package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test

class SourceAndTargetDirs : SyncTestBase() {

    override val taskConfig: TaskConfig
        get() = taskConfigWithoutBackup

    @Test
    fun empty_source_and_target_dirs_exists() {
        Assert.assertTrue(taskConfig.SOURCE_DIR.exists())
        Assert.assertTrue(taskConfig.TARGET_DIR.exists())
    }
}