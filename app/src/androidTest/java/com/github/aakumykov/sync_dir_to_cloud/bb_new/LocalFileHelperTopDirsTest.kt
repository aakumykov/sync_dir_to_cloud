package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/*
*  Задача теста - убедиться, что методы FileHelper-а
* по созданию, удалению файлов/папок работают корректно.
*
* Довольно странно из теста проверять часть его самого,
* но приходится...
* */
class LocalFileHelperTopDirsTest : LocalFileHelperTestBase() {

    // TODO: негативное тестирование

    @Before
    fun delete_source_and_target_dirs() {
        listOf(taskConfig.SOURCE_DIR, taskConfig.TARGET_DIR).forEach { dir: File ->
            dir.apply {
                deleteRecursively()
                Assert.assertFalse(this.exists())
            }
        }
    }


    //
    // Создание
    //
    @Test
    fun create_source_dir() = run {
        fileHelper.createSourceDir()
        Assert.assertTrue(taskConfig.SOURCE_DIR.exists())
    }

    @Test
    fun create_target_dir() = run {
        fileHelper.createTargetDir()
        Assert.assertTrue(taskConfig.TARGET_DIR.exists())
    }


    //
    // Чтение содержимого
    //

    /**
     * [LocalFileHelper.listSourceDir]
     */
    @Test
    fun list_source_dir() = run {
        fileHelper.createSourceDir()
        Assert.assertEquals(0, fileHelper.listSourceDir().size)

        fileHelper.createFileInSource(fileConfig.FILE_1_NAME)
        Assert.assertEquals(1, fileHelper.listSourceDir().size)
    }

    /**
     * [LocalFileHelper.listTargetDir]
     */
    @Test
    fun list_target_dir() = run {
        fileHelper.createTargetDir()
        Assert.assertEquals(0, fileHelper.listTargetDir().size)

        fileHelper.createFileInTarget(fileConfig.FILE_1_NAME)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }

    /**
     * [LocalFileHelper.listSourceDir]
     */
    @Test
    fun throws_exception_on_listing_unexistent_source_dir() = run {
        Assert.assertThrows(Exception::class.java) {
            fileHelper.listSourceDir()
        }
    }

    /**
     * [LocalFileHelper.listTargetDir]
     */
    @Test
    fun throws_exception_on_listing_unexistent_target_dir() = run {
        Assert.assertThrows(Exception::class.java) {
            fileHelper.listTargetDir()
        }
    }



    //
    // Удаление
    //

    /**
     * [LocalFileHelper.deleteSourceDirRecursively]
     */
    @Test
    fun delete_empty_source_dir() = run {
        fileHelper.createSourceDir()
        fileHelper.deleteSourceDirRecursively()
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
    }


    /**
     * [LocalFileHelper.deleteTargetDirRecursively]
     */
    @Test
    fun delete_empty_target_dir() = run {
        fileHelper.createTargetDir()
        fileHelper.deleteTargetDirRecursively()
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())
    }


    /**
     * [LocalFileHelper.deleteSourceDirRecursively]
     */
    @Test
    fun delete_source_dir_with_contents() = run {
        fileHelper.createSourceDir()

        val fileName = fileConfig.FILE_1_NAME
        val nestedFile = fileHelper.fileInSource(fileName)

        fileHelper.createFileInSource(fileName)
        Assert.assertTrue(nestedFile.exists())

        fileHelper.deleteSourceDirRecursively()
        Assert.assertFalse(nestedFile.exists())
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
    }


    /**
     * [LocalFileHelper.deleteTargetDirRecursively]
     */
    @Test
    fun delete_target_dir_with_contents() = run {
        fileHelper.createTargetDir()

        val fileName = fileConfig.FILE_1_NAME
        val nestedFile = fileHelper.fileInTarget(fileName)

        fileHelper.createFileInTarget(fileName)
        Assert.assertTrue(nestedFile.exists())

        fileHelper.deleteTargetDirRecursively()
        Assert.assertFalse(nestedFile.exists())
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())
    }


    /**
     * [LocalFileHelper.deleteAllFilesInDir]
     */
    @Test
    fun delete_all_files_in_source_dir() = run {
        fileHelper.createSourceDir()

        fileHelper.createDirInSource(fileConfig.DIR_1_NAME)
        fileHelper.createFileInSource(fileConfig.FILE_1_NAME)
        Assert.assertEquals(2, fileHelper.listSourceDir().size)

        fileHelper.deleteAllFilesInDir(taskConfig.SOURCE_DIR)
        Assert.assertEquals(0, fileHelper.listSourceDir().size)
    }


    /**
     * [LocalFileHelper.deleteAllFilesInDir]
     */
    @Test
    fun delete_all_files_in_target_dir() = run {
        fileHelper.createTargetDir()

        fileHelper.createDirInTarget(fileConfig.DIR_1_NAME)
        fileHelper.createFileInTarget(fileConfig.FILE_1_NAME)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)

        fileHelper.deleteAllFilesInDir(taskConfig.TARGET_DIR)
        Assert.assertEquals(0, fileHelper.listTargetDir().size)
    }


    // TODO: негативное тестирование везде!

    /**
     * [LocalFileHelper.deleteAllFilesInDir]
     */
    @Test
    fun delete_all_files_in_deep_dir() = run {

        val deepDirName = "1/2/3"
        val deepDir = fileHelper.dirInTarget(deepDirName)
        deepDir.mkdirs()
        Assert.assertTrue(deepDir.exists())

        val nestedFile = File(deepDir, "file1.txt")
        fileHelper.createFileOfSize(nestedFile)
        Assert.assertTrue(nestedFile.exists())

        fileHelper.deleteAllFilesInDir(deepDir)

        Assert.assertFalse(nestedFile.exists())
        Assert.assertTrue(deepDir.exists())
        Assert.assertEquals(0, fileHelper.listDir(deepDir).size)
    }


    //
    // Проверка существования
    //

    /**
     * [LocalFileHelper.isSourceDirExists]
     */
    @Test
    fun created_source_dir_exists() = run {
        fileHelper.createSourceDir()
        Assert.assertTrue(taskConfig.SOURCE_DIR.exists())
        Assert.assertTrue(fileHelper.isSourceDirExists())
    }


    /**
     * [LocalFileHelper.isTargetDirExists]
     */
    @Test
    fun created_target_dir_exists() = run {
        fileHelper.createTargetDir()
        Assert.assertTrue(taskConfig.TARGET_DIR.exists())
        Assert.assertTrue(fileHelper.isTargetDirExists())
    }


    /**
     * [LocalFileHelper.isSourceDirExists]
     */
    @Test
    fun uncreated_source_dir_not_exists() = run {
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
        Assert.assertFalse(fileHelper.isSourceDirExists())
    }


    /**
     * [LocalFileHelper.isTargetDirExists]
     */
    @Test
    fun uncreated_target_dir_not_exists() = run {
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())
        Assert.assertFalse(fileHelper.isTargetDirExists())
    }


}
// TODO: негативное тестирование везде!