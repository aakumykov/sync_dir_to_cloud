package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.TestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File

open class LocalFileHelper(
    private val taskConfig: TaskConfig,
    private val filesConfig: TestFilesConfig,
) {
    // ===============================================================
    //       0. Вспомогательные методы
    // ===============================================================
    fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)
    fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)

    fun dirInSource(dirName: String): File = File(taskConfig.SOURCE_PATH, dirName)
    fun dirInTarget(dirName: String): File = File(taskConfig.TARGET_PATH, dirName)



    // ===============================================================
    //       1. Действия с самими каталогами ИСТОЧНИКА, ПРИЁМНИКА
    // ===============================================================

    //
    // Создают каталог источника/приёмника.
    //
    fun createSourceDir() = taskConfig.SOURCE_DIR.mkdir()
    fun createTargetDir() = taskConfig.TARGET_DIR.mkdir()


    //
    //  Читает содержимое каталога источника/приёмника.
    //
    /**
     * @see [listDir]
     */
    fun listSourceDir(): Array<out File> = listDir(taskConfig.SOURCE_DIR)

    /**
     * @see [listDir]
     */
    fun listTargetDir(): Array<out File> = listDir(taskConfig.TARGET_DIR)


    //
    // Удаляют каталог источника или приёмника.
    //
    fun deleteSourceDirRecursively() = taskConfig.SOURCE_DIR.deleteRecursively()

    fun deleteTargetDirRecursively() = taskConfig.TARGET_DIR.deleteRecursively()


    //
    // Проверяет существование каталога источника/приёмника.
    //
    @Deprecated("Не нужна?")
    fun isTargetDirExists(): Boolean = taskConfig.TARGET_DIR.exists()

    @Deprecated("Не нужна?")
    fun isSourceDirExists(): Boolean = taskConfig.SOURCE_DIR.exists()


    // =======================================================================
    //      2. Файлы внутри каталогов источника, приёмника.
    // =======================================================================

    //
    // I. CRUD
    //

    //
    // Создание
    //

    // Файл простой

    fun createFileInSource(fileName: String, fileContents: ByteArray = randomBytes): File {
        return createFileWithContents(fileInSource(fileName), fileContents)
    }

    fun createFileInTarget(fileName: String, fileContents: ByteArray = randomBytes): File {
        return createFileWithContents(fileInTarget(fileName), fileContents)
    }

    private fun createFileWithContents(file: File, fileContents: ByteArray): File {
        return file.apply {
            writeBytes(fileContents)
        }
    }


    // Файл глубокий

    fun createDeepFileInSource(deepDirName: String, fileName: String, fileContents: ByteArray = randomBytes): File {
        createDirInSource(deepDirName)
        return createDeepFileWithContents(fileInSource(deepDirName), fileName, fileContents)
    }

    fun createDeepFileInTarget(deepDirName: String, fileName: String, fileContents: ByteArray = randomBytes): File {
        createDirInTarget(deepDirName)
        return createDeepFileWithContents(fileInTarget(deepDirName), fileName, fileContents)
    }

    private fun createDeepFileWithContents(deepDir: File, fileName: String, fileContents: ByteArray): File {
        return createFileWithContents(File(deepDir, fileName), fileContents)
    }


    // Каталог

    fun createDirInSource(dirName: String): File = createDir(taskConfig.SOURCE_PATH, dirName)

    fun createDirInTarget(dirName: String): File = createDir(taskConfig.TARGET_PATH, dirName)

    private fun createDir(parentDirPath: String, dirName: String): File {
        return File(parentDirPath, dirName).apply {
            mkdirs()
        }
    }



    //
    // Чтение
    //

    fun getFileContents(file: File): ByteArray = file.readBytes()

    /**
     * @throws RuntimeException при невозможности прочесть каталог по какой-либо причине.
     */
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

    @Deprecated("убрать")
    fun deleteAllFilesInDir(dir: File) {
        if (!dir.isDirectory)
            throw IllegalArgumentException("Argument is not a directory: '${dir.absolutePath}'")
        dir.listFiles()?.forEach { it.deleteRecursively() }
    }


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