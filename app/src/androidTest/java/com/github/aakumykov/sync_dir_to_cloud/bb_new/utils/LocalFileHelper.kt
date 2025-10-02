package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.TestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalTestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File

open class LocalFileHelper(
    private val taskConfig: TaskConfig,
    private val filesConfig: TestFilesConfig,
) {
    // ===============================================================
    //       1. Действия с самими каталогами ИСТОЧНИКА, ПРИЁМНИКА
    // ===============================================================

    // FIXME: раздутость вредит! Оставь только универсальные методы

    //
    // Создают каталог источника/приёмника.
    //
    // FIXME: Не тестировано. А нужно ли их тестировать?
    fun createSourceDir() = taskConfig.SOURCE_DIR.mkdir()
    fun createTargetDir() = taskConfig.TARGET_DIR.mkdir()

    //
    //  Читает содержимое каталога источника/приёмника.
    //
    // TODO: тестировать
    fun listSourceDir(): Array<out File> = listDir(taskConfig.SOURCE_DIR)
    fun listTargetDir(): Array<out File> = listDir(taskConfig.TARGET_DIR)

    //
    // Удаляют каталог источника или приёмника.
    //
    fun deleteSourceDirRecursively() = taskConfig.SOURCE_DIR.deleteRecursively()
    fun deleteTargetDirRecursively() = taskConfig.TARGET_DIR.deleteRecursively()

    //
    // Удаляют содержимое каталогов источника или приёмника.
    //
    // TODO: удалить вообще
//    fun deleteAllFilesInSource() = deleteAllFilesInDir(taskConfig.SOURCE_DIR)
//    fun deleteAllFilesInTarget() = deleteAllFilesInDir(taskConfig.TARGET_DIR)

    fun deleteAllFilesInDir(dir: File) {
        if (!dir.isDirectory)
            throw IllegalArgumentException("Argument is not a directory: '${dir.absolutePath}'")
        dir.listFiles()?.forEach { it.deleteRecursively() }
    }

    //
    // Проверяет существование каталога источника/приёмника.
    //
    @Deprecated("Не нужна?") fun isTargetDirExists(): Boolean = taskConfig.TARGET_DIR.exists()
    @Deprecated("Не нужна?") fun isSourceDirExists(): Boolean = taskConfig.SOURCE_DIR.exists()



    // =======================================================================
    //      2. Файлы внутри каталогов источника, приёмника.
    // =======================================================================

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //             А. Предопределённые файлы
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //
    // I. Собственно объекты файлов, каталогов.
    //

    // Геттеры используются потому, что taskConfig.SOURCE_PATH, taskConfig.TARGET_PATH
    // определяются в рантайме. По сути эти свойства константны.
    //
    // А может, не обязательно использовать геттеры, ведь это инструментальный тест,
    // для него специально создаётся работающее устройство?

    val sourceFile1: File get() = fileInSource(filesConfig.FILE_1_NAME)
    val sourceFile2: File get() = fileInSource(filesConfig.FILE_2_NAME)

    val targetFile1: File get() = fileInTarget(filesConfig.FILE_1_NAME)
    val targetFile2: File get() = fileInTarget(filesConfig.FILE_2_NAME)


    val sourceDir1: File get() = fileInSource(filesConfig.DIR_1_NAME)
    val sourceDir2: File get() = fileInSource(filesConfig.DIR_2_NAME)

    val targetDir1: File get() = fileInTarget(filesConfig.DIR_1_NAME)
    val targetDir2: File get() = fileInTarget(filesConfig.DIR_2_NAME)


    private val dir1InSource: File get() = dirInSource(filesConfig.DIR_1_NAME)
    private val dir2InSource: File get() = dirInSource(filesConfig.DIR_2_NAME)


    fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)
    fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)

    fun dirInSource(dirName: String): File = File(taskConfig.SOURCE_PATH, dirName)
    fun dirInTarget(dirName: String): File = File(taskConfig.TARGET_PATH, dirName)


    //
    // II. CRUD-действия
    //

    // Create
    fun createSourceFile1(): File = createFileInSource(filesConfig.FILE_1_NAME, filesConfig.FILE_1_ORIG_SIZE)
    fun createSourceFile2(): File = createFileInSource(filesConfig.FILE_2_NAME, filesConfig.FILE_2_ORIG_SIZE)

    fun createTargetFile1(): File = createFileInTarget(filesConfig.FILE_1_NAME, filesConfig.FILE_1_ORIG_SIZE)
    fun createTargetFile2(): File = createFileInTarget(filesConfig.FILE_2_NAME, filesConfig.FILE_2_ORIG_SIZE)


    fun createDir1InSource() = sourceDir1.mkdir()
    fun createDir2InSource() = sourceDir2.mkdir()

    fun createDir1InTarget() = targetDir1.mkdir()
    fun createDir2InTarget() = targetDir2.mkdir()

    // Read
    fun sourceFile1Content(): String = fileContents(sourceFile1)
    fun sourceFile2Content(): String = fileContents(sourceFile2)

    fun targetFile1Content(): String = fileContents(targetFile1)
    fun targetFile2Content(): String = fileContents(targetFile2)

    // Update
    fun modifySourceFile1(): File = createSourceFile1()
    fun modifySourceFile2(): File = createSourceFile2()

    fun modifyTargetFile1(): File = createTargetFile1()
    fun modifyTargetFile2(): File = createTargetFile2()

    // Delete
    fun deleteSourceFile1() = deleteFileFromSource(filesConfig.FILE_1_NAME)
    fun deleteSourceFile2() = deleteFileFromSource(filesConfig.FILE_2_NAME)

    fun deleteTargetFile1() = deleteFileFromTarget(filesConfig.FILE_1_NAME)
    fun deleteTargetFile2() = deleteFileFromTarget(filesConfig.FILE_2_NAME)

    fun deleteDir1InSource() = dir1InSource.deleteRecursively()
    fun deleteDir2InSource() = dir2InSource.deleteRecursively()


    //
    // III. Проверка существования
    //
    @Deprecated("избыточно") fun sourceFile1Exists(): Boolean = sourceFile1.exists()
    @Deprecated("избыточно") fun sourceFile2Exists(): Boolean = sourceFile2.exists()

    @Deprecated("избыточно") fun targetFile1Exists(): Boolean = targetFile1.exists()
    @Deprecated("избыточно") fun targetFile2Exists(): Boolean = targetFile2.exists()


    //
    // IV. Получение содержимого
    //
    val contentsOfNewSourceFile: ByteArray get() = sourceFile1.readBytes()
    val contentsOfNewTargetFile: ByteArray get() = targetFile1.readBytes()


    //
    // V. Проверка каталогов на пустоту
    //
    fun isSourceDirEmpty(): Boolean = taskConfig.SOURCE_DIR.list()?.isEmpty() ?: false
    fun isTargetDirEmpty(): Boolean = taskConfig.TARGET_DIR.list()?.isEmpty() ?: false




    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //               Б. Произвольные файлы
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //
    // I. CRUD-действия
    //

    // Create
    fun createFileInSource(name: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileOfSize(fileInSource(name), sizeKb)
    }

    /*private fun createFileInSourceWithContents(fileName: String, fileContents: ByteArray): File {
        return createFileWithContents(fileInSource(fileName), fileContents)
    }*/

    fun createFileInTarget(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileOfSize(fileInTarget(fileName), sizeKb)
    }

    fun createFileOfSize(file: File, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return file.apply {
            writeBytes(randomBytes(sizeKb))
        }
    }

    /*fun createFileWithContents(file: File, fileContents: ByteArray): File {
        return file.apply {
            writeBytes(fileContents)
        }
    }*/


    fun createDirInSource(dirName: String): File = createDir(taskConfig.SOURCE_PATH, dirName)

    fun createDirInTarget(dirName: String): File = createDir(taskConfig.TARGET_PATH, dirName)

    fun createDir(parentDirPath: String, dirName: String): File {
        return File(parentDirPath, dirName).apply {
            mkdirs()
        }
    }

    // Read
    fun fileContents(file: File): String = file.readBytes().joinToString("")

    fun listDir(dir: File): Array<out File> {
        return dir.listFiles() ?: throw RuntimeException("Cannot list '${dir.absolutePath}'")
    }


    // Update
    fun modifyFileInSource(fileName: String) = createFileInSource(fileName)

    fun modifyFileInTarget(fileName: String) = createFileInTarget(fileName)


    // Delete
    fun deleteFileFromSource(fileName: String): File = fileInSource(fileName).apply { delete() }

    fun deleteFileFromTarget(fileName: String): File = fileInTarget(fileName).apply { delete() }

    fun deleteDirFromSource(dirName: String) = dirInSource(dirName).deleteRecursively()

    fun deleteDirFromTarget(dirName: String) = dirInTarget(dirName).deleteRecursively()


    //
    // II. Проверка каталогов на пустоту
    //
    fun isDirInSourceEmpty(dirName: String): Boolean {
        return dirInSource(dirName).list()?.isEmpty() ?: false
    }

    fun isDirInTargetEmpty(dirName: String): Boolean {
        return dirInTarget(dirName).list()?.isEmpty() ?: false
    }


    companion object {
        const val DEFAULT_FILE_SIZE_KB = 10
    }
}