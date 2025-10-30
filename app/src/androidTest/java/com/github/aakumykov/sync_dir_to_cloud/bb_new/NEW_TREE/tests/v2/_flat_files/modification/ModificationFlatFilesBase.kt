package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.byte_array_joined_string.joinedString
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.TimeUnit

@RunWith(Parameterized::class)
abstract class ModificationFlatFilesBase(val numberOfRun: Int)  : SyncTestBase() {

    companion object {
        const val RUN_TEST_N_TIMES = 1
        const val DELAY_BEFORE_FILE_MODIFICATION_MS: Long = 1000

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
     *
     * Файлы в источнике и приёмнике изменились по времени [source_and_target_files_was_changed_by_time]
     * Файлы в источнике и приёмнике изменились в размере [source_and_target_files_was_changed_by_size]
     * Файл в источнике изм. по времени, а в приёмнике в размере [source_file_was_changed_by_time_and_target_by_size]
     * Файл в источнике изм. в размере, а в приёмнике по времени [source_file_was_changed_by_size_and_target_by_time]
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

        delayBeforeModification()
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

        delayBeforeModification()
        fileHelper.createFileInTarget(sFileName, newTargetFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newTargetFileData)

        doSync()
    }


    open fun source_and_target_files_was_changed_by_time() {
        createSourceFileAndSyncItWithTarget()

        delayBeforeModification()

        fileHelper.createFileInSource(sFileName, newSourceFileData)
        fileHelper.createFileInTarget(sFileName, newTargetFileData)

        assertFilesInSourceAndTargetFilesContainAndDiffer(newSourceFileData, newTargetFileData)

        doSync()
    }


    open fun source_and_target_files_was_changed_by_size() {
        createSourceFileAndSyncItWithTarget()

        fileHelper.createFileInSource(sFileName, newBigSourceFileData)
        fileHelper.createFileInTarget(sFileName, newBigTargetFileData)

        assertFilesInSourceAndTargetFilesContainAndDiffer(newBigSourceFileData, newBigTargetFileData)

        doSync()
    }

    open fun source_file_was_changed_by_time_and_target_by_size() {
        createSourceFileAndSyncItWithTarget()

        delayBeforeModification()
        fileHelper.createFileInSource(sFileName, newSourceFileData)

        fileHelper.createFileInTarget(sFileName, newBigTargetFileData)

        assertFilesInSourceAndTargetFilesContainAndDiffer(newSourceFileData, newBigTargetFileData)

        doSync()
    }


    open fun source_file_was_changed_by_size_and_target_by_time() {
        createSourceFileAndSyncItWithTarget()

        fileHelper.createFileInSource(sFileName, newBigSourceFileData)

        delayBeforeModification()
        fileHelper.createFileInTarget(sFileName, newTargetFileData)

        assertFilesInSourceAndTargetFilesContainAndDiffer(newBigSourceFileData, newTargetFileData)

        doSync()
    }


    /**
     * Эти проверки не нужны, так как методы fileHelper-а (от)тестированы!
     * Делая их, я сам себе не доверяю. С другой стороны, лишняя проверка не помешает...
     */
    private fun assertFilesInSourceAndTargetFilesContainAndDiffer(
        sourceFileData: ByteArray,
        targetFileData: ByteArray
    ) {
        Assert.assertNotEquals(
            fileHelper.getFileContents(sFile).joinedString,
            fileHelper.getFileContents(sFileInTarget).joinedString
        )

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, targetFileData)
    }


    private fun delayBeforeModification() {
        TimeUnit.MILLISECONDS.sleep(DELAY_BEFORE_FILE_MODIFICATION_MS)
    }
}