package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import org.jetbrains.annotations.TestOnly
import org.junit.Assert
import org.junit.Test

class ModificationWithoutBackup : SyncTestBase() {

    /**
     * Файл в источнике изменился перед синхронизацией [source_file_was_changed_before_sync]
     * Файл в источнике изменился после синхронизации [source_file_was_changed_after_sync]
     */

    override val taskConfig: TaskConfig get() = localToLocalNoBackupTaskConfig

    @Test
    fun source_file_was_changed_before_sync() {
        repeat(1000) {
            fileHelper.createFileInSource(sFileName, sFileData)
            assertExistsAndContains(sFile, sFileData)

            val newData = randomBytes

            fileHelper.createFileInSource(sFileName, newData)
            assertExistsAndContains(sFile, newData)
        }
    }

    @Test
    fun source_file_was_changed_after_sync() {
        repeat(50) {
            fileHelper.createFileInSource(sFileName, sFileData)
            assertSourceDirChildCount(1)
            assertTargetDirChildCount(0)
            assertExistsAndContains(sFile, sFileData)
            doSync()
            assertTargetDirChildCount(1)
            assertExistsAndContains(sFileInTarget, sFileData)

            val newData = randomBytes

            fileHelper.createFileInSource(sFileName, newData)
            assertSourceDirChildCount(1)
            assertExistsAndContains(sFile, newData)
//            doSync()
//            Assert.assertTrue(sFileInTarget.exists())
//            assertExistsAndContains(sFileInTarget, newData)
        }
    }
}