package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.WithBackupSyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes10
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetTaskBackupsDirPath
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class StaticFlatFilesWithBackup : WithBackupSyncTestBase() {

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

    private val syncTask: SyncTask
        get() = TestComponentHolder.testSyncTaskDAO.get(taskConfig.TASK_ID)!!

    private val sFileName = randomName

    private val sFileData = randomBytes10

    private val sFile get() = fileHelper.fileInSource(sFileName)

    private val sFileInTarget = fileHelper.fileInTarget(sFileName)


    private fun prepare() {
        fileHelper.createFileInSource(sFileName, sFileData)
        assertExistsAndContains(sFile, sFileData)
        doSync()
        assertExistsAndContains(sFile, sFileData)
        assertExistsAndContains(sFileInTarget, sFileData)

    }

    private fun assertExistsAndContains(file: File, contents: ByteArray) {
        Assert.assertTrue(file.exists())
        Assert.assertEquals(
            contents.joinToString(),
            fileHelper.getFileContents(file).joinToString()
        )
    }

    private fun fileWasBackedUpInTarget(fileName: String, fileData: ByteArray) {

        // Наличие каталога бекапов уровня задачи.
        syncTask.targetTaskBackupsDirPath!!.also {
            Assert.assertTrue(File(it).exists())
        }

        // Наличие сессионного каталога бекапов.
        syncTask.targetExecutionBackupDirPath!!.also {
            Assert.assertTrue(File(it).exists())

            // Наличие забекапленного файла в нём
            File(it, fileName).also { file ->
                assertExistsAndContains(file, fileData)
            }
        }
    }


    @Before
    fun create_file_in_source_and_sync_with_target() {
        prepare()
    }


    // 1 --- 1
    // x --- 1
    // sync
    // x --- x {1}
    @Test
    fun source_file_deleted() {
//        prepare()

        fileHelper.deleteFileFromSource(sFileName)
        Assert.assertFalse(sFile.exists())

        doSync()

        Assert.assertEquals(1, fileHelper.targetDirItemsCount())

        Assert.assertFalse(sFile.exists())
        Assert.assertFalse(sFileInTarget.exists())

        fileWasBackedUpInTarget(sFileName, sFileData)
    }


    // [1] --- [1]
    // [1]* --- [1]
    // sync
    // [1]* --- [1]* {1}
    @Test
    fun source_file_modified() {
//        prepare()

        // Пересоздаю файл в источнике и проверяю, что он с новым содержимым.
        val newData = randomBytes
        fileHelper.createFileInSource(sFileName, newData)
        Assert.assertNotEquals(sFileData, fileHelper.getFileContents(sFile))

        doSync()

        Assert.assertEquals(2, fileHelper.targetDirItemsCount())

        assertExistsAndContains(sFile, newData)
        assertExistsAndContains(sFileInTarget, newData)

        fileWasBackedUpInTarget(sFileName, sFileData)
    }


    // 1 --- 1
    // 1 --- x
    // sync
    // 1 --- 1
    @Test
    fun target_file_deleted() {
//        prepare()

        fileHelper.deleteFileFromTarget(sFileName)

        doSync()

        Assert.assertEquals(1, fileHelper.sourceDirItemsCount())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(1, fileHelper.targetDirItemsCount())
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    // 1 --- 1
    // 1 --- 1*
    // sync
    // 1 --- 1 {1*}
    @Test
    fun target_file_modified() {
//        prepare()

        val newData = randomBytes10
        fileHelper.createFileInTarget(sFileName, newData)

        doSync()

        // Проверяю, что содержимое файлов стало разным.
        Assert.assertNotEquals(
            fileHelper.getFileContents(sFile),
            fileHelper.getFileContents(sFileInTarget)
        )

        Assert.assertEquals(1, fileHelper.sourceDirItemsCount())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(2, fileHelper.targetDirItemsCount())
        assertExistsAndContains(sFileInTarget, sFileData)

        fileWasBackedUpInTarget(sFileName, newData)
    }
}