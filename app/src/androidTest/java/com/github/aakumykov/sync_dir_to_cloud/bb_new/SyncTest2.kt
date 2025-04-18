package com.github.aakumykov.sync_dir_to_cloud.bb_new

import androidx.room.util.recursiveFetchHashMap
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion.DeleteAllFilesInSourceScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion.DeleteAllFilesInTargetScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSync
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SyncTest2 : StorageAccessTestCase() {

    private val fileHelper = LocalFileHelperHolder.fileHelper

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


    //
    // Не выдумываю фантастических сценариев, воспроизвожу реалистичный
    //
    /*
    1) Файл появляется в источнике.
    1.1) Меняется в источнике
    1.2) Меняется в приёмнике
    1.3) Меняется и там, и там
    1.4) Удаляется в источнике
    1.5) Удаляется в приёмнике
    1.6) Удаляется и там, и там
    2) Файл появляется в приёмнике.
    2ю)

     */

    @Test
    fun sync_two_empty_dirs() = run {
        sync()
        checkSourceDirIsEmpty()
        checkTargetDirIsEmpty()
    }


    private fun checkSourceDirIsEmpty() {
        checkDirIsEmpty(SyncSide.SOURCE)
    }

    private fun checkTargetDirIsEmpty() {
        checkDirIsEmpty(SyncSide.TARGET)
    }

    private fun checkDirIsEmpty(syncSide: SyncSide) {
        Assert.assertEquals(
            0,
            when(syncSide) {
                SyncSide.SOURCE -> fileHelper.listSourceDir()
                SyncSide.TARGET -> fileHelper.listTargetDir()
            }.size
        )
    }

    private fun sync() = run {
        scenario(RunSync())
    }

    @Test
    fun new_file_in_source() {
        fileHelper.createSourceFile1()
        sync()
        Assert.assertEquals(fileHelper.sourceFile1Content(),fileHelper.targetFile1Content())
    }

    @Test
    fun modified_file_in_source_unchanged_in_target() {

    }
}