package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.kaspersky.kaspresso.device.logcat.LogcatBufferSize
import java.io.File
import java.io.IOException

open class LocalFileHelper(private val taskConfig: TaskConfig) {

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


    fun countFilesInSource(): Int = listSourceDir().size


    fun countFilesInTarget(): Int = listTargetDir().size



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

        if (file.exists()) {
            Log.d(TAG,"STORAGE_STATE, Удаляется файл перед созданием: ${file.absolutePath}")
            if (!file.delete())
                throw RuntimeException("Cannot delete file '${file.absolutePath}'")
        }

        return file.apply {
            createNewFile()
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

    // TODO: тест
    fun dirInSourceItemsCount(dirName: String): Int = listDirInSource(dirName).size

    // TODO: тест
    fun dirInTargetItemsCount(dirName: String): Int = listDirInTarget(dirName).size



    // Update
    fun modifyFileInSource(dirName: String, fileName: String): File = createDeepFileInSource(dirName, fileName)

    fun modifyFileInTarget(dirName: String, fileName: String): File = createDeepFileInTarget(dirName, fileName)


    // Delete

    @Throws(IOException::class)
    fun deleteFileFromSource(fileName: String): File = deleteFile(fileInSource(fileName))

    @Throws(IOException::class)
    fun deleteFileFromTarget(fileName: String): File = deleteFile(fileInTarget(fileName))

    @Throws(IOException::class)
    fun deleteDeepFileFromSource(dirName: String, fileName: String): File = deleteFile(deepFileInSource(dirName, fileName))

    @Throws(IOException::class)
    fun deleteDeepFileFromTarget(dirName: String, fileName: String): File = deleteFile(deepFileInTarget(dirName, fileName))

    @Throws(IOException::class)
    private fun deleteFile(file: File): File {
        return if (!file.delete()) throw IOException("'${file.absolutePath}'")
        else file
    }


    @Throws(IOException::class)
    fun deleteDirFromSource(dirName: String): File {
        return dirInSource(dirName).let {
            if (!it.delete()) throw IOException("Error deleting '${it.absolutePath}'")
            else it
        }
    }

    @Throws(IOException::class)
    fun deleteDirFromTarget(dirName: String): File {
        return dirInTarget(dirName).let {
            if (!it.delete()) throw IOException("Error deleting '${it.absolutePath}'")
            else it
        }
    }


    fun deleteAllFilesInDir(dir: File) {
        if (!dir.isDirectory)
            throw IllegalArgumentException("Argument is not a directory: '${dir.absolutePath}'")
        dir.listFiles()?.forEach { it.deleteRecursively() }
    }

    companion object {
        val TAG = LocalFileHelper::class.java.simpleName
    }
}