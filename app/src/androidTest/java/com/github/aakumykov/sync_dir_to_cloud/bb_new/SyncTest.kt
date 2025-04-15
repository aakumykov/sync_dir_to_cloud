package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.TestFileManager
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Test

class SyncTest : TestCase() {

    private val fileManager = TestFileManager()

    //
    //
    //
    @Test
    fun when_sync_one_file_in_source_then_that_file_appears_in_tartet() = run {

        scenario(CreateLocalTaskScenario())
        scenario(RunSyncScenario())

//        Assert.assertTrue(fileManager.targetFile.exists())
    }
}