package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.LocalFileHelper
import org.junit.Assert
import org.junit.Before
import java.io.File
import java.util.concurrent.TimeUnit

abstract class SyncTestBase : StorageAccessTestCase() {

    abstract val taskConfig: TaskConfig

    private val afterSyncSleepTimeoutMs: Long = 1000

    protected val fileHelper get() = LocalFileHelper(taskConfig)


    @Before
    fun reCreateLocalTask() = run {
        scenario(DeleteLocalTaskScenario())
        scenario(CreateLocalTaskScenario(taskConfig))
    }

    // TODO: проверять, что БД перед запуском чиста...

    @Before
    fun prepareSourceAndTargetDirs() = run {
        fileHelper.deleteSourceDirRecursively()
        fileHelper.deleteTargetDirRecursively()
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())

        fileHelper.createSourceDir()
        fileHelper.createTargetDir()
        Assert.assertTrue(taskConfig.SOURCE_DIR.exists())
        Assert.assertTrue(taskConfig.TARGET_DIR.exists())
    }


    protected fun doSync(delayAfterWork: Boolean = true) = run {

        scenario(RunSyncScenario(taskConfig.TASK_ID))

        if (delayAfterWork)
            TimeUnit.MILLISECONDS.sleep(afterSyncSleepTimeoutMs)
    }

    protected fun assertExistsAndEmpty(fileOrDir: File) {
        Assert.assertTrue(fileOrDir.exists())
        Assert.assertTrue(fileOrDir.isEmpty)
    }
}