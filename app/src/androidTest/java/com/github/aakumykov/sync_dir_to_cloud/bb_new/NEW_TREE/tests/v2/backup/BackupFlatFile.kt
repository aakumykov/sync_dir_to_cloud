package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.backup

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Test
import java.io.File

// FIXME: тестировать
val File.childrenCount: Int get() = list()!!.size

/**
 * Проверяется бекап файла в корне каталога Источника.
 */
class BackupFlatFile : BackupTestBase() {

    /**
     * Бекап файла, удалённого в источнике [deleted_file_in_source_is_backuped]
     * Бекап файла, удалённого в приёмнике [deleted_file_in_target_]
     *
     * Бекап файла, изменённого в источнике [modified_file_in_source_is_backuped]
     * Бекап файла, изменённого в приёмнике [modified_file_in_target_is_backuped]
     */

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(
            LocalToLocalSyncWithoutBackupTaskConfig())


    @Test
    fun deleted_file_in_source_is_backuped() {
        fileHelper.createFileInSource(sFileName)
        doSync()
        fileHelper.deleteFileFromSource(sFileName)
        doSync()

        assertSourceDirChildCount(0)
        assertTargetDirChildCount(1)

        assertTaskBackupsDirInTargetChildCount(1)
        assertExecutionBackupDirInTargetChildCount(1)

        assertExecutionBackupDirExistsAndContains(sFileName, sFileData)
    }


    /**
     * Эта ситуация проверяется в тесте удаления (каком? такого ещё нет)
     */
    @Test
    fun deleted_file_in_target_() {

    }


    @Test
    fun modified_file_in_source_is_backuped() {
        fileHelper.createFileInSource(sFileName, sFileData)
        doSync()
        fileHelper.createFileInSource(sFileName, newSourceFileData)
        doSync()

        assertSourceDirChildCount(1)
        assertTargetDirChildCount(2)

        assertTaskBackupsDirInTargetChildCount(1)
        assertExecutionBackupDirInTargetChildCount(1)
        assertExecutionBackupDirExistsAndContains(sFileName, sFileData)
    }
}
