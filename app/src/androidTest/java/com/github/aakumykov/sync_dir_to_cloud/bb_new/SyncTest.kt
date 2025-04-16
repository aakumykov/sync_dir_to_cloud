package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.CreateOneSourceFileScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SyncTest : StorageAccessTestCase() {

    private val fileHelper = LocalFileHelperHolder.fileHelper

    // TODO: передавать каталог источника, не равный системному!

    @Before
    fun deleteAllFilesInSource() {
        // TODO: сделать!
    }

    @After
    fun deleteAllFilesInTarget() {
        // TODO: сделать!
    }

    @Test
    fun syncOneFile() = run {
        runBlocking {
            scenario(CreateLocalTaskScenario())
            scenario(CreateOneSourceFileScenario())
            scenario(RunSyncScenario())
            Assert.assertTrue(fileHelper.targetFile1Exists())
        }
    }
}