package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.config.TaskConfig
import java.io.File
import kotlin.random.Random

class TestFilesCreator(private val taskConfig: TaskConfig) {

    fun createFileInSource(fileName: String, sizeKb: Int = 1024): File {
        return createFile(fileInSource(fileName), sizeKb)
    }

    fun createFileInTarget(fileName: String, sizeKb: Int = 1024): File {
        return createFile(fileInTarget(fileName), sizeKb)
    }

    fun createDirInSource(dirName: String): File {
        return createDir(taskConfig.SOURCE_PATH, dirName)
    }

    fun createDirInTarget(dirName: String): File {
        return createDir(taskConfig.TARGET_PATH, dirName)
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
            writeBytes(Random.nextBytes(sizeKb))
        }
    }

    /*fun randomFileName(prefix: String = "file"): String {
        return "${prefix}-${randomString}"
    }

    private val randomString: String
        get() = randomUUID.split("-").first()*/

    fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)
    fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)
}


/*@AssistedFactory
interface TestFilesCreator2AssistedFactory {
    fun create(syncTask: SyncTask): TestFilesCreator2
}*/
