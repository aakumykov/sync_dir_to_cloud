package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File
import kotlin.random.Random

open class LocalTestFileManager(
    private val taskConfig: TaskConfig = LocalTaskConfig,
    private val fileConfig: FileConfig = LocalFileCofnig,
) {

    val sourceFile1: File
        get() = fileInSource(fileConfig.FILE_1_NAME)

    val sourceFile2: File
        get() = fileInSource(fileConfig.FILE_2_NAME)


    val targetFile1: File
        get() = fileInTarget(fileConfig.FILE_1_NAME)

    val targetFile2: File
        get() = fileInTarget(fileConfig.FILE_2_NAME)


    val sourceFileContents: ByteArray
        get() = sourceFile1.readBytes()

    val targetFileContents: ByteArray
        get() = targetFile1.readBytes()



    fun createSourceFile1(): File {
        return createFileInSource(fileConfig.FILE_1_NAME, fileConfig.FILE_1_SIZE)
    }

    fun createSourceFile2(): File {
        return createFileInSource(fileConfig.FILE_2_NAME, fileConfig.FILE_2_SIZE)
    }


    fun createTargetFile1(): File {
        return createFileInTarget(fileConfig.FILE_1_NAME, fileConfig.FILE_1_SIZE)
    }

    fun createTargetFile2(): File {
        return createFileInTarget(fileConfig.FILE_2_NAME, fileConfig.FILE_2_SIZE)
    }


    fun deleteSourceFile1() {
        deleteFileFromSource(fileConfig.FILE_1_NAME)
    }

    fun deleteSourceFile2() {
        deleteFileFromSource(fileConfig.FILE_2_NAME)
    }


    fun deleteTargetFile1() {
        deleteFileFromTarget(fileConfig.FILE_1_NAME)
    }

    fun deleteTargetFile2() {
        deleteFileFromTarget(fileConfig.FILE_2_NAME)
    }


    fun sourceFile1Exists(): Boolean = sourceFile1.exists()
    fun sourceFile2Exists(): Boolean = sourceFile2.exists()
    fun targetFile1Exists(): Boolean = targetFile1.exists()
    fun targetFile2Exists(): Boolean = targetFile2.exists()



    fun createFileInSource(name: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFile(fileInSource(name), sizeKb)
    }

    fun createFileInSource(fileName: String, fileContents: ByteArray): File {
        return createFile(fileInSource(fileName), fileContents)
    }

    fun createFileInTarget(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFile(fileInTarget(fileName), sizeKb)
    }


    fun createDirInSource(dirName: String): File {
        return createDir(taskConfig.SOURCE_PATH, dirName)
    }

    fun createDirInTarget(dirName: String): File {
        return createDir(taskConfig.TARGET_PATH, dirName)
    }


    fun deleteFileFromSource(fileName: String): File {
        return fileInSource(fileName).apply {
            delete()
        }
    }

    fun deleteFileFromTarget(fileName: String): File {
        return fileInTarget(fileName).apply {
            delete()
        }
    }


    private fun createDir(parentDirPath: String, dirName: String): File {
        return File(parentDirPath, dirName).apply {
            mkdir()
        }
    }

    private fun createFile(dirPath: String, fileName: String, sizeKb: Int): File {
        return File(dirPath, fileName).apply {
            createNewFile()
            writeBytes(Random.nextBytes(sizeKb))
        }
    }

    private fun createFile(file: File, sizeKb: Int): File {
        return file.apply {
            writeBytes(randomBytes(sizeKb))
        }
    }

    private fun createFile(file: File, fileContents: ByteArray): File {
        return file.apply {
            writeBytes(fileContents)
        }
    }

    /*fun randomFileName(prefix: String = "file"): String {
        return "${prefix}-${randomString}"
    }

    private val randomString: String
        get() = randomUUID.split("-").first()*/

    fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)

    fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)


    fun modifyFileInSource(fileName: String, fileContents: ByteArray): File {
        return createFileInSource(fileName, fileContents)
    }

    fun modifyFileInTarget(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileInTarget(fileName, sizeKb)
    }

    fun deleteSourceDir() {
        taskConfig.SOURCE_DIR.deleteRecursively()
    }

    fun deleteTargetDir() {
        taskConfig.TARGET_DIR.deleteRecursively()
    }

    fun createSourceDir() {
        taskConfig.SOURCE_DIR.mkdir()
    }

    fun createTargetDir() {
        taskConfig.TARGET_DIR.mkdir()
    }

    companion object {
        const val DEFAULT_FILE_SIZE_KB = 10
    }
}


/*@AssistedFactory
interface TestFilesCreator2AssistedFactory {
    fun create(syncTask: SyncTask): TestFilesCreator2
}*/
