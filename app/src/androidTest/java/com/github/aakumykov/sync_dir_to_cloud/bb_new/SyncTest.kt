package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.CreateFirstSourceFileScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.CreateFirstTargetFileScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.CreateSecondSourceFileScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.CreateSourceFile
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.MarkTargetFileAsNew
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion.DeleteAllFilesInSourceScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion.DeleteAllFilesInTargetScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSync
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SyncTest : StorageAccessTestCase() {

    private val fileHelper = LocalFileHelperHolder.fileHelper
    private val fileConfig = LocalFileCofnig

    // TODO: передавать каталог источника, не равный системному!

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


    @Test
    fun syncOneFile() = run {
        runBlocking {
            scenario(CreateFirstSourceFileScenario())
            scenario(RunSync())
            // TODO: сравнивать содержимое файлов
            Assert.assertTrue(fileHelper.targetFile1Exists())
            // TODO: выводить содержимое файлов до и после
        }
    }


    @Test
    fun syncTwoFiles() = run {
        runBlocking {
            scenario(CreateFirstSourceFileScenario())
            scenario(CreateSecondSourceFileScenario())

            scenario(RunSync())

            Assert.assertTrue(fileHelper.targetFile1Exists())
            Assert.assertTrue(fileHelper.targetFile2Exists())
        }
    }


    @Test
    fun sync_one_file_overwriting_in_target() = run {
        runTest {
            scenario(CreateFirstSourceFileScenario())
            scenario(CreateFirstTargetFileScenario())

            Assert.assertNotEquals(
                fileHelper.sourceFile1Content(),
                fileHelper.targetFile1Content(),
            )

            scenario(RunSync())

            Assert.assertTrue(fileHelper.sourceFile1Exists())
            Assert.assertTrue(fileHelper.targetFile1Exists())

            Assert.assertEquals(
                fileHelper.sourceFile1Content(),
                fileHelper.targetFile1Content(),
            )
        }
    }

    //
    // Прежний файл - Прежний файл
    //
    @Test
    fun sync_two_unchanged_files() {
        run {
            syncOneFile()

            val oldModificationTime: Long = fileHelper.targetFile1.lastModified()
            val oldContent = fileHelper.targetFile1Content()

            scenario(RunSync())

            val newModificationTime: Long = fileHelper.targetFile1.lastModified()
            val newContent = fileHelper.targetFile1Content()

            Assert.assertEquals(oldModificationTime, newModificationTime)
            Assert.assertEquals(oldContent, newContent)
        }
    }

    //
    // Прежний файл - Новый файл
    //
    @Test
    fun sync_unchanged_and_new_file() = run {
        scenario(CreateSourceFile(fileConfig.FILE_1_NAME))
        scenario(RunSync())
        Assert.assertTrue(fileHelper.targetFile1Exists())

        fileHelper.modifyTargetFile1()
        scenario(MarkTargetFileAsNew(fileConfig.FILE_1_NAME))
        scenario(RunSync())

        Assert.assertEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content(),
        )
    }

    @Test
    fun kak_ya_hochu() {
        /*
        // Синхронизация Отсутствующего с Отсутствующим
        sync()
        Assert.assertEquals(0, fileHelper.listSource())
        Assert.assertEquals(0, fileHelper.listTarget())

        // Синхронизация Отсутствующего с Прежним
        val file = createTargetFile(fileName)
        sync()
        markTargetFileAs(UNCHANGED, fileName)
        sync()
        // checkSourceIsEmpty()
        Assert.assertEquals(0, fileHelper.listSource())
        checkFilesEquals(file, fileHelper.targetFile1)


        // Синхронизация Отсутствующего с Новым
        val file = createTargetFile(fileName)
        markTargetFileAs(NEW, fileName)
        sync()
        // checkSourceIsEmpty()
        Assert.assertEquals(0, fileHelper.listSource())
        checkFilesEquals(file, fileHelper.targetFile1)


        // Синхронизация Отсутствующего с Изменённым
        val file = createTargetFile(fileName)
        markTargetFileAs(MODIFIED, fileName)
        sync()
        // checkSourceIsEmpty()
        Assert.assertEquals(0, fileHelper.listSource())
        checkFilesEquals(file, fileHelper.targetFile1)


        // Простая синхронизация (Синхронизация
        createSourceFile(fileName)
        sync()
        checkFilesAreEquals(fileName, fileName)


        // Синхронизация Прежнего с Новым
        createSourceFile(fileName)

        sync()
        checkFilesAreEquals(fileName, fileName)

        markTargetFileAs(NEW, fileName)

        sync()
        checkFilesAreEquals(fileName, fileName)


        // Синхронизация Прежнего с Изменённым
        createSourceFile(fileName)

        sync()
        checkFilesAreEquals(fileName, fileName)

        modifyTargetFile(fileName)

        sync()
        checkFilesAreEquals(fileName, fileName)


        // Синхронизация Прежнего с Удалённым
        createSourceFile(fileName)

        sync()
        checkFilesAreEquals(fileName, fileName)

        deleteTargetFile(fileName)

        sync()
        checkFilesAreEquals(fileName, fileName)
         */
    }
}