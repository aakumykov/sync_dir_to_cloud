package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File
import kotlin.random.Random

class TestFileManager(
    private val taskConfig: TaskConfig = LocalTaskConfig,
    private val fileCofnig: FileConfig = LocalFileCofnig,
) {

    val sourceFile: File
        get() = fileInSource(fileCofnig.FILE_1_NAME)

    val targetFile: File
        get() = fileInTarget(fileCofnig.FILE_1_NAME)


    val sourceFileContents: ByteArray
        get() = sourceFile.readBytes()

    val targetFileContents: ByteArray
        get() = targetFile.readBytes()


    fun createFileInSource(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFile(fileInSource(fileName), sizeKb)
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

    companion object {
        const val DEFAULT_FILE_SIZE_KB = 10
    }
}


/*@AssistedFactory
interface TestFilesCreator2AssistedFactory {
    fun create(syncTask: SyncTask): TestFilesCreator2
}*/
