package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalForwardSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import org.junit.Assert
import org.junit.Before
import java.io.File

abstract class LocalFileHelperTestBase : StorageAccessTestCase() {

    protected val taskConfig = LocalToLocalForwardSyncWithoutBackupTaskConfig()
    protected val fileHelper = LocalFileHelper(taskConfig)

    @Before
    fun delete_source_and_target_dirs() {
        listOf(taskConfig.SOURCE_DIR, taskConfig.TARGET_DIR).forEach { dir: File ->
            dir.apply {
                deleteRecursively()
                Assert.assertFalse(this.exists())
            }
        }
    }
}