package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test

class StaticFlatFilesSyncTestWithoutBackup : SyncTestBase()  {

    /**
     * Пустой файл - нет ничего [empty_file_in_source_and_no_files_in_target]
     * Нет ничего - пустой файл [no_files_in_source_and_empty_file_in_target]
     *
     * Пустой файл - одноимённый пустой файл [same_name_empty_files_in_src_and_tgt]
     * Пустой файл - разноимённый пустой файл [diff_names_empty_files_in_source_and_target]
     *
     *
     * Непустой файл - нет ничего [data_file_in_source_and_no_files_in_target]
     * Нет ничего - непустой файл [no_files_in_source_and_data_file_in_target]
     *
     * Непустой файл - одноимённый непустой файл [same_name_data_files_in_source_and_target]
     * Непустой файл - разноимённый непустой файл [diff_names_data_files_in_source_and_target]
     */

    @Test
    fun empty_file_in_source_and_no_files_in_target() {
        val fileName = randomName
        val data = byteArrayOf()

        val sourceFile = fileHelper.createFileInSource(fileName, data)
        val targetFile = fileHelper.fileInTarget(fileName)

        doSync()

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        Assert.assertTrue(sourceFile.exists())
        Assert.assertTrue(targetFile.exists())

        Assert.assertEquals(
            fileHelper.getFileContents(sourceFile).joinToString(),
            fileHelper.getFileContents(targetFile).joinToString()
        )

        Assert.assertTrue(fileHelper.getFileContents(sourceFile).isEmpty())
        Assert.assertTrue(fileHelper.getFileContents(targetFile).isEmpty())
    }

    @Test
    fun no_files_in_source_and_empty_file_in_target() {
        val fileName = randomName
        val data = byteArrayOf()

        val sourceFile = fileHelper.fileInSource(fileName)
        val targetFile = fileHelper.createFileInTarget(fileName, data)

        doSync()

        Assert.assertEquals(0, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        Assert.assertFalse(sourceFile.exists())
        Assert.assertTrue(targetFile.exists())

        Assert.assertTrue(fileHelper.getFileContents(targetFile).isEmpty())
    }

    @Test
    fun same_name_empty_files_in_src_and_tgt() {
        val fileName = randomName
        val data = byteArrayOf()

        val sourceFile = fileHelper.createFileInSource(fileName, data)
        val targetFile = fileHelper.createFileInTarget(fileName, data)

        doSync()

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        Assert.assertTrue(sourceFile.exists())
        Assert.assertTrue(targetFile.exists())

        Assert.assertEquals(
            fileHelper.getFileContents(sourceFile).joinToString(),
            fileHelper.getFileContents(targetFile).joinToString(),
        )
    }

    @Test
    fun diff_names_empty_files_in_source_and_target() {
        val sFileName = randomName
        val tFileName = randomName

        val data = byteArrayOf()

        val sFile = fileHelper.createFileInSource(sFileName, data)
        val sFileInTarget = fileHelper.fileInTarget(sFileName)

        val tFile = fileHelper.createFileInTarget(tFileName, data)
        val tFileInSource = fileHelper.fileInSource(tFileName)

        doSync()

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)

        Assert.assertTrue(sFile.exists())
        Assert.assertTrue(tFile.exists())

        Assert.assertTrue(sFileInTarget.exists())
        Assert.assertFalse(tFileInSource.exists())

        Assert.assertEquals(
            data.joinToString(),
            fileHelper.getFileContents(sFile).joinToString()
        )
        Assert.assertEquals(
            data.joinToString(),
            fileHelper.getFileContents(tFile).joinToString()
        )

        Assert.assertEquals(
            data.joinToString(),
            fileHelper.getFileContents(sFileInTarget).joinToString()
        )
    }


    @Test
    fun data_file_in_source_and_no_files_in_target() {
        val fileName = randomName
        val data = randomBytes

        val sFile = fileHelper.createFileInSource(fileName, data)
        val tFile = fileHelper.fileInTarget(fileName)

        doSync()

        Assert.assertTrue(sFile.exists())
        Assert.assertEquals(
            data.joinToString(),
            fileHelper.getFileContents(sFile).joinToString()
        )

        Assert.assertTrue(tFile.exists())
        Assert.assertEquals(
            data.joinToString(),
            fileHelper.getFileContents(tFile).joinToString()
        )
    }

    @Test
    fun no_files_in_source_and_data_file_in_target() {
        val fileName = randomName
        val data = randomBytes

        val sFile = fileHelper.fileInSource(fileName)
        val tFile = fileHelper.createFileInTarget(fileName, data)

        doSync()

        Assert.assertFalse(sFile.exists())
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)

        Assert.assertTrue(tFile.exists())
        Assert.assertEquals(
            data.joinToString(),
            fileHelper.getFileContents(tFile).joinToString()
        )
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }

    @Test
    fun same_name_data_files_in_source_and_target() {
        val fileName = randomName
        val sData = randomBytes
        val tData = randomBytes

        val sFile = fileHelper.createFileInSource(fileName, sData)
        val tFile = fileHelper.createFileInTarget(fileName, tData)

        doSync()

        Assert.assertTrue(sFile.exists())
        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(
            sData.joinToString(),
            fileHelper.getFileContents(sFile).joinToString()
        )

        Assert.assertTrue(tFile.exists())
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
        Assert.assertEquals(
            sData.joinToString(),
            fileHelper.getFileContents(tFile).joinToString()
        )
    }

    @Test
    fun diff_names_data_files_in_source_and_target() {
        val sFileName = randomName
        val tFileName = randomName

        val sData = randomBytes
        val tData = randomBytes

        val sFile = fileHelper.createFileInSource(sFileName, sData)
        val sFileInTarget = fileHelper.fileInTarget(sFileName)

        val tFile = fileHelper.createFileInTarget(tFileName, tData)
        val tFileInSource = fileHelper.fileInSource(tFileName)

        doSync()

        Assert.assertTrue(sFile.exists())
        Assert.assertTrue(sFileInTarget.exists())

        Assert.assertTrue(tFile.exists())
        Assert.assertFalse(tFileInSource.exists())

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)

        Assert.assertEquals(
            sData.joinToString(),
            fileHelper.getFileContents(sFile).joinToString()
        )
        Assert.assertEquals(
            tData.joinToString(),
            fileHelper.getFileContents(tFile).joinToString()
        )

        Assert.assertEquals(
            sData.joinToString(),
            fileHelper.getFileContents(sFileInTarget).joinToString()
        )
    }
}