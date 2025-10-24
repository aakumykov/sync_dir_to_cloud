package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.TimeUnit

@RunWith(Parameterized::class)
class ModificationWithoutBackup(val numberOfRun: Int) : SyncTestBase() {

    companion object {
        const val RUN_TEST_N_TIMES = 3
        const val DELAY_BEFORE_SOURCE_FILE_MODIFICATION_MS: Long = 1000
        
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Int> = List(RUN_TEST_N_TIMES) { it }
    }

    override val taskConfig: TaskConfig get() = localToLocalNoBackupTaskConfig

    
    /**
     * Файл в источнике изменился в размере [source_file_was_changed_by_size]
     * Файл в источнике изменился по времени (с тем же размером) [source_file_was_changed_by_time]
     *
     * Файл в приёмнике изменился в размере [target_file_was_changed_by_size]
     * Файл в приёмнике изменился по времени (с тем же размером) [target_file_was_changed_by_time]
     */

    
    @Test
    fun empty_test_() {
        Assert.assertTrue(true)
    }

    @Test
    fun source_file_was_changed_by_size() {
        change_source_file_between_sync { modifiedFileName ->
            val newData = randomBytes(20)
            fileHelper.createFileInSource(modifiedFileName, newData)
            newData
        }
    }

    @Test
    fun source_file_was_changed_by_time() {
        change_source_file_between_sync { modifiedFileName ->
            TimeUnit.MILLISECONDS.sleep(DELAY_BEFORE_SOURCE_FILE_MODIFICATION_MS)
            val newData = randomBytes
            fileHelper.createFileInSource(modifiedFileName, newData)
            newData
        }
    }


    @Test
    fun target_file_was_changed_by_size() {
        change_target_file_between_sync { modifiedFileName ->
            val newData = randomBytes(20)
            fileHelper.createFileInTarget(modifiedFileName, newData)
            newData
        }
    }

    @Test
    fun target_file_was_changed_by_time() {
        change_target_file_between_sync { modifiedFileName ->
            TimeUnit.MILLISECONDS.sleep(DELAY_BEFORE_SOURCE_FILE_MODIFICATION_MS)
            val newData = randomBytes
            fileHelper.createFileInTarget(modifiedFileName, newData)
            newData
        }
    }



    private fun change_target_file_between_sync(fileModificationBlock: (modifiedFileName: String) -> ByteArray) {

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

    private fun change_source_file_between_sync(fileModificationBlock: (modifiedFileName: String) -> ByteArray) {

        createSourceFileAndSyncItWithTarget()

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

    private fun createSourceFileAndSyncItWithTarget() {

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