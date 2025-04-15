package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.RunSync
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.TestFileManager
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Assert
import org.junit.Test

class SyncTest : TestCase() {

    private val fileManager = TestFileManager()

    //
    //
    //
    @Test
    fun when_sync_one_file_in_source_then_that_file_appears_in_tartet() = run {
        scenario(CreateTaskScenario())
        scenario(RunSync())
        Assert.assertTrue(fileManager.targetFile.exists())
    }
}