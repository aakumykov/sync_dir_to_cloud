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

    // Create
    fun createFileInSource(name: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileOfSize(fileInSource(name), sizeKb)
    }

    fun createFileInTarget(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileOfSize(fileInTarget(fileName), sizeKb)
    }

    /*private fun createFileInSourceWithContents(fileName: String, fileContents: ByteArray): File {
    return createFileWithContents(fileInSource(fileName), fileContents)
}*/

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


    fun createDirInSource(dirName: String): File = createDir(taskConfig.SOURCE_PATH, dirName)

    fun createDirInTarget(dirName: String): File = createDir(taskConfig.TARGET_PATH, dirName)

    fun createDir(parentDirPath: String, dirName: String): File {
        return File(parentDirPath, dirName).apply {
            mkdirs()
        }
    }

    // Read
    fun fileContents(file: File): String = file.readBytes().joinToString("")

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


    //
    // Вспомогательные методы
    //
    // TODO: тестировать!
    fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)
    fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)

    fun dirInSource(dirName: String): File = File(taskConfig.SOURCE_PATH, dirName)
    fun dirInTarget(dirName: String): File = File(taskConfig.TARGET_PATH, dirName)


    companion object {
        const val DEFAULT_FILE_SIZE_KB = 10
    }
}