package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SyncTest : StorageAccessTestCase() {

    private val sleepTimeout: Long = 750

    private val fileHelper = LocalFileHelper()


    @Before
    fun reCreateLocalTask() = run {
        scenario(DeleteLocalTaskScenario())
        scenario(CreateLocalTaskScenario())
    }


    @Before
    fun prepareSourceAndTargetDirs() = run {
        fileHelper.createSourceDir()
        Assert.assertTrue(fileHelper.sourceDirExists())

        fileHelper.createTargetDir()
        Assert.assertTrue(fileHelper.targetDirExists())

        fileHelper.deleteAllFilesInSource()
        Assert.assertTrue(fileHelper.sourceDirIsEmpty())

        fileHelper.deleteAllFilesInTarget()
        Assert.assertTrue(fileHelper.targetDirIsEmpty())
    }


    //
    // Не выдумываю фантастических сценариев, воспроизвожу реалистичный
    //
    /**
    0) Нет файлов ни там, ни там [sync_two_empty_dirs]

    1) Файл появляется в источнике [new_file_in_source]
    1.1) Меняется в источнике [modified_file_in_source_unchanged_in_target]
    1.2) Меняется в приёмнике [modified_file_in_target_and_unchanged_in_source]
    1.3) Меняется и там, и там [modified_file_in_source_and_modified_in_target]
    1.4) Удаляется в источнике [file_deleted_in_target]
    1.5) Удаляется в приёмнике [file_deleted_in_source]
    1.6) Удаляется и там, и там [file_deleted_in_source_and_target]
    2) Файл появляется в приёмнике [new_file_in_target]
    ...)

     */

    @Test
    fun sync_two_empty_dirs() = run {
        sync()
        checkSourceDirIsEmpty()
        checkTargetDirIsEmpty()
    }


    @Test
    fun new_file_in_source() {
        fileHelper.createSourceFile1()
        sync()
        Assert.assertEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content()
        )
    }


    // Ошибочный при массовом запуске
    // Ошибочный при индивидуальном запуске
    @Test
    fun e1_e2_modified_file_in_source_unchanged_in_target() = run {

        new_file_in_source()

        Thread.sleep(sleepTimeout)

        fileHelper.modifySourceFile1()

        Assert.assertNotEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content()
        )

        syncAndCheckFile1Equals()
    }


    // Ошибочный при массовом запуске
    // Ошибочный при индивидуальном запуске
    @Test
    fun e1_e2_modified_file_in_target_and_unchanged_in_source() {

        new_file_in_source()

        Thread.sleep(sleepTimeout)

        fileHelper.modifyTargetFile1()
        Assert.assertNotEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content()
        )

        sync()
        syncAndCheckFile1Equals()
    }


    // Запустился при массовом запуске после [sync_two_empty_dirs].
    // Запустился при индивидуальном запуске.

    // Ошибочный при массовом запуске после [sync_two_empty_dirs].
    // Запустился при индивидуальном запуске.
    // Ошибочный при индивидуальном запуске.
    @Test
    fun e1_e2_modified_file_in_source_and_modified_in_target() {

        new_file_in_source()

        Thread.sleep(sleepTimeout)

        fileHelper.modifyTargetFile1()
        fileHelper.modifySourceFile1()
        Assert.assertNotEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content()
        )

        sync()
        syncAndCheckFile1Equals()
    }


    @Test
    fun file_deleted_in_target() {

        new_file_in_source()

        fileHelper.deleteTargetFile1()
        Assert.assertFalse(fileHelper.targetFile1Exists())

        sync()
        syncAndCheckFile1Equals()
    }


    @Test
    fun file_deleted_in_source() {

        new_file_in_source()

        fileHelper.deleteSourceFile1()
        Assert.assertFalse(fileHelper.sourceFile1Exists())

        sync()
        Assert.assertFalse(fileHelper.targetFile1Exists())
    }


    @Test
    fun file_deleted_in_source_and_target() {

        new_file_in_source()

        fileHelper.deleteSourceFile1()
        Assert.assertFalse(fileHelper.sourceFile1Exists())

        fileHelper.deleteTargetFile1()
        Assert.assertFalse(fileHelper.targetFile1Exists())

        sync()
        Assert.assertFalse(fileHelper.sourceFile1Exists())
        Assert.assertFalse(fileHelper.targetFile1Exists())
    }


    @Test
    fun new_file_in_target() {

        new_file_in_source()

        fileHelper.createTargetFile2()

        sync()
        Assert.assertFalse(fileHelper.sourceFile2Exists())
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
        scenario(RunSyncScenario())
    }

    private fun syncAndCheckFile1Equals() {
        sync()
        Assert.assertEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content()
        )
    }
}