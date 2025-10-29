package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.TimeUnit

@RunWith(Parameterized::class)
abstract class ModificationFlatFilesBase(val numberOfRun: Int)  : SyncTestBase() {

    companion object {
        const val RUN_TEST_N_TIMES = 1
        const val DELAY_BEFORE_SOURCE_FILE_MODIFICATION_MS: Long = 1000

        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Int> = List(RUN_TEST_N_TIMES) { it }
    }

    /**
     * Файл в источнике изменился в размере [source_file_was_changed_by_size]
     * Файл в источнике изменился по времени (с тем же размером) [source_file_was_changed_by_time]
     *
     * Файл в приёмнике изменился в размере [target_file_was_changed_by_size]
     * Файл в приёмнике изменился по времени (с тем же размером) [target_file_was_changed_by_time]
     */


    open fun empty_test_() {
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }


    open fun source_file_was_changed_by_size() {
        createSourceFileAndSyncItWithTarget()
        fileHelper.createFileInSource(sFileName, newBigSourceFileData)
        doSync()
    }


    open fun source_file_was_changed_by_time() {
        createSourceFileAndSyncItWithTarget()

        TimeUnit.MILLISECONDS.sleep(DELAY_BEFORE_SOURCE_FILE_MODIFICATION_MS)
        fileHelper.createFileInSource(sFileName, newSourceFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)

        doSync()
    }


    open fun target_file_was_changed_by_size() {
        createSourceFileAndSyncItWithTarget()

        fileHelper.createFileInTarget(sFileName, newBigTargetFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newBigTargetFileData)

        doSync()
    }


    open fun target_file_was_changed_by_time() {
        createSourceFileAndSyncItWithTarget()

        TimeUnit.MILLISECONDS.sleep(DELAY_BEFORE_SOURCE_FILE_MODIFICATION_MS)
        fileHelper.createFileInTarget(sFileName, newTargetFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newTargetFileData)

        doSync()
    }



    protected fun change_target_file_between_sync(fileModificationBlock: (modifiedFileName: String) -> ByteArray) {

        createSourceFileAndSyncItWithTarget()

        // Меняю файл в приёмнике, синхронизированный из источника.
        val newData = fileModificationBlock.invoke(sFileName)

        fileHelper.createFileInTarget(sFileName, newData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newData)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }

    protected fun change_source_file_between_sync(
        fileModificationBlock: (modifiedFileName: String) -> ByteArray,
        resultCheckingBlock: () -> Unit
    ) {

        val newData = fileModificationBlock.invoke(sFileName)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newData)
    }

    protected fun createSourceFileAndSyncItWithTarget() {

        fileHelper.createFileInSource(sFileName, sFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(0)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }
}