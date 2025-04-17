package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.CreateOneSourceFileScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion.DeleteAllFilesInSourceScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion.DeleteAllFilesInTargetScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SyncTest : StorageAccessTestCase() {

    private val fileHelper = LocalFileHelperHolder.fileHelper

    // TODO: передавать каталог источника, не равный системному!

    @Before
    fun deleteLocalTask() = run {
        scenario(DeleteLocalTaskScenario())
        scenario(CreateLocalTaskScenario())
    }


    @Before
    fun deleteAllFilesInSourceAndTarget() = run {
        scenario(DeleteAllFilesInSourceScenario())
        scenario(DeleteAllFilesInTargetScenario())
    }


    @Test
    fun syncOneFile() = run {
        runBlocking {
            scenario(CreateOneSourceFileScenario())
            scenario(RunSyncScenario())
            Assert.assertTrue(fileHelper.targetFile1Exists())
            // TODO: выводить содержимое файлов до и после
        }
    }
}