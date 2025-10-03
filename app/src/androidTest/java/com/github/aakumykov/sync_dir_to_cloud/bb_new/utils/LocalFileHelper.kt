package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.TestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

open class LocalFileHelper(
    private val taskConfig: TaskConfig,
    private val filesConfig: TestFilesConfig,
) {
    // ===============================================================
    //       0. Вспомогательные методы
    // ===============================================================
    fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)
    fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)

    fun deepFileInSource(dirName: String, fileName: String): File = File(fileInSource(dirName), fileName)
    fun deepFileInTarget(dirName: String, fileName: String): File = File(fileInTarget(dirName), fileName)

    fun dirInSource(dirName: String): File = File(taskConfig.SOURCE_PATH, dirName)
    fun dirInTarget(dirName: String): File = File(taskConfig.TARGET_PATH, dirName)

    fun deepDirInSource(parentDirName: String, childDirName: String): File = File(dirInSource(parentDirName), childDirName)
    fun deepDirInTarget(parentDirName: String, childDirName: String): File = File(dirInTarget(parentDirName), childDirName)


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

    /**
     * @throws RuntimeException при невозможности прочесть каталог по какой-либо причине.
     */
    private fun listDir(dir: File): Array<out File> {
        return dir.listFiles() ?: throw RuntimeException("Cannot list '${dir.absolutePath}'")
    }


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

    /**
     * Создаёт файл в указанном "глубоком" каталоге в ИСТОЧЕНИКЕ,
     * создавая попутно и сам этот каталог.
     */
    fun createDeepFileInSource(deepDirName: String, fileName: String, fileContents: ByteArray = randomBytes): File {
        createDirInSource(deepDirName)
        return createDeepFileWithContents(fileInSource(deepDirName), fileName, fileContents)
    }

    /**
     * Создаёт файл в указанном "глубоком" каталоге в ПРИЁМНИКЕ,
     * создавая попутно и сам этот каталог.
     */
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

    // Файла
    fun getFileContents(file: File): ByteArray = file.readBytes()


    // Каталога
    fun listDirInSource(dirName: String): Array<out File> {
        return dirInSource(dirName).let {
            it.listFiles() ?: throw RuntimeException("Cannot list '${it.absolutePath}'")
        }
    }

    fun listDirInTarget(dirName: String): Array<out File> {
        return dirInTarget(dirName).let {
            it.listFiles() ?: throw RuntimeException("Cannot list '${it.absolutePath}'")
        }
    }


    // Update
    fun modifyFileInSource(dirName: String, fileName: String): File = createDeepFileInSource(dirName, fileName)

    fun modifyFileInTarget(dirName: String, fileName: String): File = createDeepFileInTarget(dirName, fileName)


    // Delete

    @Throws(IOException::class)
    fun deleteFileFromSource(fileName: String): File = fileInSource(fileName).apply {
        if (this.exists()) delete()
        else throw FileNotFoundException("'${this.absolutePath}'")
    }

    @Throws(IOException::class)
    fun deleteFileFromTarget(fileName: String): File = fileInTarget(fileName).apply {
        if (this.exists()) delete()
        else throw FileNotFoundException("'${this.absolutePath}'")
    }

    @Throws(IOException::class)
    fun deleteDeepFileFromSource(dirName: String, fileName: String): File = deepFileInSource(dirName, fileName).apply {
        if (this.exists()) delete()
        else throw FileNotFoundException("'${this.absolutePath}'")
    }

    @Throws(IOException::class)
    fun deleteDeepFileFromTarget(dirName: String, fileName: String): File = deepFileInTarget(dirName, fileName).apply {
        if (this.exists()) delete()
        else throw FileNotFoundException("'${this.absolutePath}'")
    }


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