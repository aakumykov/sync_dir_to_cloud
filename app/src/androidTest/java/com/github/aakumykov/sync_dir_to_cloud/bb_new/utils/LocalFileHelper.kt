package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File
import kotlin.random.Random

open class LocalFileHelper(
    private val taskConfig: TaskConfig = LocalTaskConfig(),
    private val fileConfig: FileConfig = LocalFileCofnig,
) {
    val sourceDir: File
        get() = taskConfig.SOURCE_DIR

    val targetDir: File
        get() = taskConfig.TARGET_DIR

    val sourceFile1: File
        get() = fileInSource(fileConfig.FILE_1_NAME)

    val sourceFile2: File
        get() = fileInSource(fileConfig.FILE_2_NAME)


    val targetFile1: File
        get() = fileInTarget(fileConfig.FILE_1_NAME)

    val targetFile2: File
        get() = fileInTarget(fileConfig.FILE_2_NAME)


    val sourceDir1: File
        get() = fileInSource(fileConfig.DIR_1_NAME)

    val sourceDir2: File
        get() = fileInSource(fileConfig.DIR_2_NAME)


    val targetDir1: File
        get() = fileInTarget(fileConfig.DIR_1_NAME)

    val targetDir2: File
        get() = fileInTarget(fileConfig.DIR_2_NAME)


    val twoLevelDirName: String
        get() = fileConfig.TWO_LEVEL_DIR_NAME


    private val sourceFileContents: ByteArray
        get() = sourceFile1.readBytes()

    private val targetFileContents: ByteArray
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


    fun modifySourceFile1(): File {
        return createSourceFile1()
    }

    fun modifySourceFile2(): File {
        return createSourceFile2()
    }

    fun modifyTargetFile1(): File {
        return createTargetFile1()
    }

    fun modifyTargetFile2(): File {
        return createTargetFile2()
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



    fun deleteAllFilesInDir(dir: File) {
        if (!dir.isDirectory)
            throw IllegalArgumentException("Argument is not a directory: '${dir.absolutePath}'")

        dir.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }



    fun sourceFile1Exists(): Boolean = sourceFile1.exists()

    fun sourceFile2Exists(): Boolean = sourceFile2.exists()

    fun targetFile1Exists(): Boolean = targetFile1.exists()

    fun targetFile2Exists(): Boolean = targetFile2.exists()



    fun createFileInSource(name: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFile(fileInSource(name), sizeKb)
    }

    private fun createFileInSource(fileName: String, fileContents: ByteArray): File {
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


    private fun deleteFileFromSource(fileName: String): File {
        return fileInSource(fileName).apply {
            delete()
        }
    }

    private fun deleteFileFromTarget(fileName: String): File {
        return fileInTarget(fileName).apply {
            delete()
        }
    }


    private fun createDir(parentDirPath: String, dirName: String): File {
        return File(parentDirPath, dirName).apply {
            mkdirs()
        }
    }

    private fun createFile(dirPath: String, fileName: String, sizeKb: Int): File {
        return File(dirPath, fileName).apply {
            createNewFile()
            writeBytes(Random.nextBytes(sizeKb))
        }
    }

    fun createFile(file: File, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return file.apply {
            writeBytes(randomBytes(sizeKb))
        }
    }

    private fun createFile(file: File, fileContents: ByteArray): File {
        return file.apply {
            writeBytes(fileContents)
        }
    }


    private fun fileInSource(fileName: String): File = File(taskConfig.SOURCE_PATH, fileName)

    private fun fileInTarget(fileName: String): File = File(taskConfig.TARGET_PATH, fileName)


    private fun modifyFileInSource(fileName: String, fileContents: ByteArray): File {
        return createFileInSource(fileName, fileContents)
    }

    private fun modifyFileInTarget(fileName: String, sizeKb: Int = DEFAULT_FILE_SIZE_KB): File {
        return createFileInTarget(fileName, sizeKb)
    }

    private fun deleteSourceDir() {
        taskConfig.SOURCE_DIR.deleteRecursively()
    }

    private fun deleteTargetDir() {
        taskConfig.TARGET_DIR.deleteRecursively()
    }

    // FIXME: Не тестировано. А нужно ли их тестировать?
    fun createSourceDir() {
        taskConfig.SOURCE_DIR.mkdir()
    }

    fun createTargetDir() {
        taskConfig.TARGET_DIR.mkdir()
    }

    fun sourceFile1Content(): String = fileContents(sourceFile1)
    fun sourceFile2Content(): String = fileContents(sourceFile2)

    fun targetFile1Content(): String = fileContents(targetFile1)
    fun targetFile2Content(): String = fileContents(targetFile2)


    // TODO: тестировать
    fun listSourceDir(): Array<out File> {
        return listDir(taskConfig.SOURCE_DIR)
    }

    // TODO: тестировать
    fun listTargetDir(): Array<out File> {
        return listDir(taskConfig.TARGET_DIR)
    }

    // TODO: тестировать
    private fun listDir(dir: File): Array<out File> {
        return dir.listFiles()
            ?: throw RuntimeException("Cannot list '${dir.absolutePath}'")
    }

    fun targetDirExists(): Boolean = targetDir.exists()

    fun sourceDirExists(): Boolean = sourceDir.exists()

    fun deleteAllFilesInSource() {
        deleteAllFilesInDir(sourceDir)
    }

    fun deleteAllFilesInTarget() {
        deleteAllFilesInDir(targetDir)
    }

    fun sourceDirIsEmpty(): Boolean {
        return sourceDir.list()?.isEmpty() ?: false
    }

    fun targetDirIsEmpty(): Boolean {
        return targetDir.list()?.isEmpty() ?: false
    }

    fun createDir1InSource() {
        sourceDir1.mkdir()
    }

    fun createDir2InSource() {
        sourceDir2.mkdir()
    }

    fun createDir1InTarget() {
        targetDir1.mkdir()
    }

    fun createDir2InTarget() {
        targetDir2.mkdir()
    }

    fun deleteSourceDir1() {
        sourceDir1.deleteRecursively()
    }

    fun dirInSource(dirName: String): File {
        return File(taskConfig.SOURCE_PATH, dirName)
    }

    fun createFileInDir(dir: File, fileName: String) {
        val file =  File(dir, fileName)
        createFile(file)
    }

    fun dirInTarget(dirName: String): File {
        return File(taskConfig.TARGET_PATH, dirName)
    }

    fun deleteDirFromSource(dirName: String) {
        dirInSource(dirName).deleteRecursively()
    }

    fun deleteDirFromTarget(dirName: String) {
        dirInTarget(dirName).deleteRecursively()
    }

    fun fileContents(file: File): String {
        return file.readBytes().joinToString("")
    }

    companion object {
        const val DEFAULT_FILE_SIZE_KB = 10
    }
}