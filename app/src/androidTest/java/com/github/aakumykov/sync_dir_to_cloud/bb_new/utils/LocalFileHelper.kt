package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.TestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalTestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File

open class LocalFileHelper(
    private val taskConfig: TaskConfig = LocalToLocalTaskConfig(),
    private val filesConfig: TestFilesConfig = LocalTestFilesConfig,
) {
    // ===============================================================
    //       1. Действия с самими каталогами ИСТОЧНИКА, ПРИЁМНИКА
    // ===============================================================

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

    private fun listDir(dir: File): Array<out File> {
        return dir.listFiles() ?: throw RuntimeException("Cannot list '${dir.absolutePath}'")
    }

    //
    // Рекурсивно удаляют каталог источника или приёмника.
    //
    fun deleteAllFilesInSource() = deleteAllFilesInDir(taskConfig.SOURCE_DIR)
    fun deleteAllFilesInTarget() = deleteAllFilesInDir(taskConfig.TARGET_DIR)

    fun deleteAllFilesInDir(dir: File) {
        if (!dir.isDirectory)
            throw IllegalArgumentException("Argument is not a directory: '${dir.absolutePath}'")
        dir.listFiles()?.forEach { it.deleteRecursively() }
    }

    //
    // Проверяет существование каталога источника/приёмника.
    //
    fun isTargetDirExists(): Boolean = taskConfig.TARGET_DIR.exists()
    fun isSourceDirExists(): Boolean = taskConfig.SOURCE_DIR.exists()



    // =======================================================================
    //      2. Предопределённые файлы в источнике, приёмнике.
    // =======================================================================

    val newSourceFile1: File get() = fileInSource(filesConfig.FILE_1_NAME)
    val newSourceFile2: File get() = fileInSource(filesConfig.FILE_2_NAME)

    val newTargetFile1: File get() = fileInTarget(filesConfig.FILE_1_NAME)
    val newTargetFile2: File get() = fileInTarget(filesConfig.FILE_2_NAME)


    val newSourceDir1: File get() = fileInSource(filesConfig.DIR_1_NAME)
    val newSourceDir2: File get() = fileInSource(filesConfig.DIR_2_NAME)

    val newTargetDir1: File get() = fileInTarget(filesConfig.DIR_1_NAME)
    val newTargetDir2: File get() = fileInTarget(filesConfig.DIR_2_NAME)


    private val newDir1InSource: File get() = dirInSource(filesConfig.DIR_1_NAME)
    private val newDir2InSource: File get() = dirInSource(filesConfig.DIR_2_NAME)


    private fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)
    private fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)

    fun dirInSource(dirName: String): File = File(taskConfig.SOURCE_PATH, dirName)
    fun dirInTarget(dirName: String): File = File(taskConfig.TARGET_PATH, dirName)


    // =======================================================================
    //      3. Действия с файлами в каталогах источника, приёмника.
    // =======================================================================
    val contentsOfNewSourceFile: ByteArray get() = newSourceFile1.readBytes()
    val contentsOfNewTargetFile: ByteArray get() = newTargetFile1.readBytes()


    fun createSourceFile1(): File = createFileInSource(filesConfig.FILE_1_NAME, filesConfig.FILE_1_ORIG_SIZE)
    fun createSourceFile2(): File = createFileInSource(filesConfig.FILE_2_NAME, filesConfig.FILE_2_ORIG_SIZE)

    fun createTargetFile1(): File = createFileInTarget(filesConfig.FILE_1_NAME, filesConfig.FILE_1_ORIG_SIZE)
    fun createTargetFile2(): File = createFileInTarget(filesConfig.FILE_2_NAME, filesConfig.FILE_2_ORIG_SIZE)


    fun modifySourceFile1(): File = createSourceFile1()
    fun modifySourceFile2(): File = createSourceFile2()

    fun modifyTargetFile1(): File = createTargetFile1()
    fun modifyTargetFile2(): File = createTargetFile2()


    fun deleteSourceFile1() = deleteFileFromSource(filesConfig.FILE_1_NAME)
    fun deleteSourceFile2() = deleteFileFromSource(filesConfig.FILE_2_NAME)

    fun deleteTargetFile1() = deleteFileFromTarget(filesConfig.FILE_1_NAME)
    fun deleteTargetFile2() = deleteFileFromTarget(filesConfig.FILE_2_NAME)


    fun sourceFile1Exists(): Boolean = fileInSource(filesConfig.FILE_1_NAME).exists()
    fun sourceFile2Exists(): Boolean = fileInSource(filesConfig.FILE_2_NAME).exists()

    fun targetFile1Exists(): Boolean = fileInTarget(filesConfig.FILE_1_NAME).exists()
    fun targetFile2Exists(): Boolean = fileInTarget(filesConfig.FILE_2_NAME).exists()





    //
    // Создают файл в источнике/приёмнике с заданными именем, размером/содержимым.
    //
    private fun createFileInSource(name: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileOfSize(fileInSource(name), sizeKb)
    }

    private fun createFileInSourceWithContents(fileName: String, fileContents: ByteArray): File {
        return createFileWithContents(fileInSource(fileName), fileContents)
    }

    private fun createFileInTarget(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileOfSize(fileInTarget(fileName), sizeKb)
    }


    //
    // Создают предопределённые каталоги в источнике, приёмнике.
    //
    fun createDir1InSource() = newSourceDir1.mkdir()
    fun createDir2InSource() = newSourceDir2.mkdir()

    fun createDir1InTarget() = newTargetDir1.mkdir()
    fun createDir2InTarget() = newTargetDir2.mkdir()



    //
    // Создают каталог в источнике, приёмнике с заданным именем.
    //
    fun createDirInSource(dirName: String): File = createDir(taskConfig.SOURCE_PATH, dirName)
    fun createDirInTarget(dirName: String): File = createDir(taskConfig.TARGET_PATH, dirName)

    private fun createDir(parentDirPath: String, dirName: String): File {
        return File(parentDirPath, dirName).apply {
            mkdirs()
        }
    }



    //
    // Удаляют файл с именем из источника/приёмника.
    //
    private fun deleteFileFromSource(fileName: String): File = fileInSource(fileName).apply { delete() }
    private fun deleteFileFromTarget(fileName: String): File = fileInTarget(fileName).apply { delete() }



    //
    // Создают реальные файлы из объекта File.
    //
    fun createFileOfSize(file: File, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return file.apply {
            writeBytes(randomBytes(sizeKb))
        }
    }

    fun createFileWithContents(file: File, fileContents: ByteArray): File {
        return file.apply {
            writeBytes(fileContents)
        }
    }







    //
    // Возвращают содержимое файлов в источнике, приёмнике.
    //
    fun sourceFile1Content(): String = fileContents(newSourceFile1)
    fun sourceFile2Content(): String = fileContents(newSourceFile2)

    fun targetFile1Content(): String = fileContents(newTargetFile1)
    fun targetFile2Content(): String = fileContents(newTargetFile2)

    fun fileContents(file: File): String = file.readBytes().joinToString("")









    //
    // Проверяет пуст ли каталог источника/приёмника.
    //
    fun isSourceDirEmpty(): Boolean = taskConfig.SOURCE_DIR.list()?.isEmpty() ?: false
    fun isTargetDirEmpty(): Boolean = taskConfig.TARGET_DIR.list()?.isEmpty() ?: false




    fun deleteDir1InSource() = newDir1InSource.deleteRecursively()
    fun deleteDir2InSource() = newDir2InSource.deleteRecursively()


    fun deleteDirFromSource(dirName: String) = dirInSource(dirName).deleteRecursively()
    fun deleteDirFromTarget(dirName: String) = dirInTarget(dirName).deleteRecursively()


    companion object {
        const val DEFAULT_FILE_SIZE_KB = 10
    }
}