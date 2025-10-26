package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.backup

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * Проверяется бекап файла в корне каталога Источника.
 *
 * ==== sync ====>
 */
class BackupFlatFile : BackupTestBase() {

    /**
     * Бекап файла, удалённого в источнике [deleted_file_in_source_will_backuped_in_target]
     * Бекап файла, удалённого в приёмнике [deleted_file_in_target_will_be_copied_again]
     *
     * Бекап файла, изменённого в источнике [modified_by_size_file_in_source_will_backuped_in_target]
     * Бекап файла, изменённого в источнике [modified_by_time_file_in_source_will_backuped_in_target]
     *
     * Бекап файла, изменённого в приёмнике [modified_by_size_file_in_target_will_backuped_in_target]
     * Бекап файла, изменённого в приёмнике [modified_by_time_file_in_target_will_backuped_in_target]
     */

    override val taskConfig: TaskConfig get() = localToLocalWithBackupTaskConfig


    @Test
    fun deleted_file_in_source_will_backuped_in_target() {
        createSourceFileAndSyncToTarget()

        fileHelper.deleteFileFromSource(sFileName)
        doSync()

        assertSourceDirChildCount(0)
        assertTargetDirChildCount(1)

        assertTaskBackupsDirInTargetChildCount(1)
        assertExecutionBackupDirInTargetChildCount(1)

        assertTargetExecutionBackupDirExistsAndContains(sFileName, sFileData)
    }


    /**
     * Эта же ситуация проверяется в тесте удаления с включенными бекапами.
     */
    @Test
    fun deleted_file_in_target_will_be_copied_again() {
        createSourceFileAndSyncToTarget()

        fileHelper.deleteFileFromTarget(sFileName)
        assertTargetDirChildCount(0)

        doSync()

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    @Test
    fun modified_by_size_file_in_source_will_backuped_in_target() {
        createSourceFileAndSyncToTarget()

        modifyAndSyncSourceFileWithData {
            randomBytes(20)
        }
    }


    @Test
    fun modified_by_time_file_in_source_will_backuped_in_target() {
        createSourceFileAndSyncToTarget()

        modifyAndSyncSourceFileWithData {
            sleep(1)
            randomBytes
        }
    }


    @Test
    fun modified_by_size_file_in_target_will_backuped_in_target() {
        createSourceFileAndSyncToTarget()

        modifyAndSyncTargetFileWithData {
            randomBytes(20)
        }
    }


    @Test
    fun modified_by_time_file_in_target_will_backuped_in_target() {
        createSourceFileAndSyncToTarget()

        modifyAndSyncTargetFileWithData {
            sleep(1)
            randomBytes
        }
    }


    private fun createSourceFileAndSyncToTarget() {
        fileHelper.createFileInSource(sFileName, sFileData)
        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    private fun modifyAndSyncSourceFileWithData(newDataSupplier: Supplier<ByteArray>) {

        val data = newDataSupplier.get()

        // Изменяю исходный файл
        fileHelper.createFileInSource(sFileName, data)

        // Убеждаюсь, что изменился именно исходный файл и не появилось лишнего.
        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, data)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)

        doSync()

        // Убеждаюсь, что после синхронизации...
        // ...файл в источнике прежний и нет лишенго,
        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, data)

        // ...файл в приёмнике обновился,
        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, data)

        // ...в приёмнике бекапнут старый файл и нет лишнего.
        assertTaskBackupsDirInTargetChildCount(1)
        assertExecutionBackupDirInTargetChildCount(1)
        assertTargetExecutionBackupDirExistsAndContains(sFileName, sFileData)
    }


    private fun modifyAndSyncTargetFileWithData(newDataSupplier: Supplier<ByteArray>) {

        val newData = newDataSupplier.get()

        fileHelper.createFileInTarget(sFileName, newData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newData)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, sFileData)

        assertTaskBackupsDirInTargetChildCount(1)
        assertExecutionBackupDirInTargetChildCount(1)
        assertTargetExecutionBackupDirExistsAndContains(sFileName, newData)
    }

    private fun sleep(seconds: Long) {
        TimeUnit.SECONDS.sleep(seconds)
    }
}
