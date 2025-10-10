package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import org.junit.Assert
import org.junit.Test
import java.io.File

class StaticFlatFilesWithBackup : SyncTestBase() {

    override val taskConfig: TaskConfig
        get() = taskConfigWithBackup

    private val syncTask: SyncTask
        get() = TestComponentHolder.testSyncTaskDAO.get(taskConfig.TASK_ID)!!

    private val sFileName = randomName
    private val tFileName = randomName

    private val sFileData = randomBytes
    private val tFileData = randomBytes

    private val sFile get() = fileHelper.fileInSource(sFileName)
    private val tFile get() = fileHelper.fileInSource(tFileName)

    private val sFileInTarget = fileHelper.fileInTarget(sFileName)


    private fun prepare() {
        fileHelper.createFileInSource(sFileName, sFileData)
        existsAndContainsData(sFile, sFileData)
        doSync()
        existsAndContainsData(sFile, sFileData)
        existsAndContainsData(sFileInTarget, sFileData)

    }

    private fun existsAndContainsData(file: File, contents: ByteArray) {
        Assert.assertTrue(file.exists())
        Assert.assertEquals(
            contents.joinToString(),
            fileHelper.getFileContents(file).joinToString()
        )
    }


    /**
     * Файлы в корне, с бекапом.
     *
     * Исходное состояние: (файл1) --- синхронизирован ---> (файл2)
     *
     * ТЕСТЫ
     *
     * Файл в источнике удалён [source_file_deleted]
     * Файл в источнике изменён [source_file_modified]
     *
     * Файл в приёмнике удалён [target_file_deleted]
     * Файл в приёмнике изменён [target_file_modified]
     *
     *
     */

    private val taskBackupsDirInTarget
        get() = fileHelper.dirInTarget(syncTask.targetTaskBackupDirName!!)


    // 1 --- 1
    // x --- 1
    // sync
    // x --- x {1}
    @Test
    fun source_file_deleted() {
        prepare()

        fileHelper.deleteFileFromSource(sFileName)
        doSync()

        Assert.assertFalse(sFile.exists())
        Assert.assertFalse(sFileInTarget.exists())

        Assert.assertTrue(taskBackupsDirInTarget.exists())
    }
}