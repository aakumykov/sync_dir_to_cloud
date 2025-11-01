package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_has_no_extra_files.assertDeepDirHasOnlyOneChildAtAllLevels
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count.childrenCount
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceTaskBackupsDirAbsolutePath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetExecutionBackupDirAbsolutePath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetTaskBackupsDirAbsolutePath
import org.junit.Assert
import org.junit.Before
import java.io.File
import java.util.concurrent.TimeUnit

abstract class SyncTestBase : StorageAccessTestCase() {

    protected abstract val taskConfig: TaskConfig

    protected open val inDeviceChangesRelaxationTimeoutMs: Long = 1000

    protected val fileHelper get() = LocalFileHelper(taskConfig)

    private val syncTask: SyncTask
        get() = TestComponentHolder.testSyncTaskDAO.get(taskConfig.TASK_ID)!!

    //
    // Настройки файлов
    //

    protected val commonFileName = randomName
    protected val commonFileInSource = fileHelper.fileInSource(commonFileName)
    protected val commonFileInTarget = fileHelper.fileInTarget(commonFileName)

    private val binFileName: String get() = "${randomName}.bin"

    protected val sFileName = binFileName
    protected val tFileName = binFileName

    protected val sFile = fileHelper.fileInSource(sFileName)
    protected val tFile = fileHelper.fileInTarget(tFileName)

    protected val commonFileData: ByteArray = freshNotEmptyData
    protected val sFileData: ByteArray = freshNotEmptyData
    protected val tFileData: ByteArray = freshNotEmptyData

    protected val newSourceFileData: ByteArray = freshNotEmptyData
    protected val newTargetFileData: ByteArray = freshNotEmptyData

    protected val newBigSourceFileData: ByteArray = randomBytes(20)
    protected val newBigTargetFileData: ByteArray = randomBytes(20)

    protected val sFileInTarget = fileHelper.fileInTarget(sFileName)
    protected val tFileInSource = fileHelper.fileInSource(tFileName)

    protected val emptyData: ByteArray = byteArrayOf()
    protected val freshNotEmptyData: ByteArray get() = randomBytes


    //
    // Настройки каталогов
    //
    protected val commonDirName = randomName

    protected val sDirName = randomName
    protected val tDirName = randomName

    protected val commonDirInSource = fileHelper.dirInSource(commonDirName)
    protected val commonDirInTarget = fileHelper.dirInTarget(commonDirName)

    // FIXME: неудачное название. Я воспринял её как "источник", а не "каталог в источнике",
    //  которым она является на самом деле.
    protected val sDir = fileHelper.dirInSource(sDirName)
    protected val tDir = fileHelper.dirInTarget(tDirName)

    protected val sDirInTarget = fileHelper.dirInTarget(sDirName)
    protected val tDirInSource = fileHelper.dirInSource(tDirName)


    //
    // "Глубокие" каталоги
    //
    protected val commonDeepDirName = randomDeepDirName
    protected val commonDeepDirInSource = fileHelper.dirInSource(commonDeepDirName)
    protected val commonDeepDirInTarget = fileHelper.dirInTarget(commonDeepDirName)

    protected val sDeepDirName = randomDeepDirName
    protected val tDeepDirName = randomDeepDirName

    protected val sDeepDir = fileHelper.dirInSource(sDeepDirName)
    protected val tDeepDir = fileHelper.dirInTarget(tDeepDirName)

    protected val sDeepDirInTarget = fileHelper.dirInTarget(sDeepDirName)
    protected val tDeepDirInSource = fileHelper.dirInSource(tDeepDirName)


    //
    // "Глубокие" файлы // TODO: разная глубина
    //
    protected val sDeepFile = fileHelper.deepFileInSource(sDeepDirName, sFileName)
    protected val tDeepFile = fileHelper.deepFileInTarget(tDeepDirName, tFileName)

    protected val sDeepFileInTarget = fileHelper.deepFileInTarget(sDeepDirName, sFileName)
    protected val tDeepFileInSource = fileHelper.deepFileInSource(tDeepDirName, tFileName)




    @Before
    fun reCreateLocalTask() = run {
        scenario(DeleteLocalTaskScenario())
        scenario(CreateLocalTaskScenario(taskConfig))
    }

    // TODO: проверять, что БД перед запуском чиста...

    @Before
    fun prepareSourceAndTargetDirs() = run {

        // Удаляю источник и приёмник.
        fileHelper.deleteSourceDirRecursively()
        fileHelper.deleteTargetDirRecursively()
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())

        // Создаю источник и приёмник.
        fileHelper.createSourceDir()
        fileHelper.createTargetDir()
        Assert.assertTrue(taskConfig.SOURCE_DIR.exists())
        Assert.assertTrue(taskConfig.TARGET_DIR.exists())

        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }


    protected fun doSync(delayAfterWork: Boolean = false) = run {

        scenario(RunSyncScenario(taskConfig.TASK_ID))

        if (delayAfterWork)
            TimeUnit.MILLISECONDS.sleep(inDeviceChangesRelaxationTimeoutMs)
    }

    protected fun assertExistsAndEmpty(fileOrDir: File) {
        Assert.assertTrue(fileOrDir.exists())
        Assert.assertTrue(fileOrDir.isEmpty)
    }

    protected fun assertExistsAndContains(file: File, contents: ByteArray) {
        Assert.assertTrue(file.exists())
        Assert.assertEquals(
            contents.joinToString(),
            fileHelper.getFileContents(file).joinToString()
        )
    }

    protected fun assertTargetDirChildCount(count: Int) {
        Assert.assertEquals(count, fileHelper.countFilesInTarget())
    }

    protected fun assertSourceDirChildCount(count: Int) {
        Assert.assertEquals(count, fileHelper.countFilesInSource())
    }



    protected fun assertTargetExecutionBackupDirExistsAndContains(fileName: String, fileContents: ByteArray) {

        val targetExecutionBackupsDir = File(
            syncTask.targetTaskBackupsDirAbsolutePath!!,
            syncTask.targetExecutionBackupDirName!!
        )
        Assert.assertTrue(targetExecutionBackupsDir.exists())

        val backedUpFile = File(targetExecutionBackupsDir, fileName)
        assertExistsAndContains(backedUpFile, fileContents)
    }

    protected fun assertSourceExecutionBackupDirExistsAndContains(fileName: String, fileContents: ByteArray) {

        val sourceExecutionBackupsDir = File(
            syncTask.sourceTaskBackupsDirAbsolutePath!!,
            syncTask.sourceExecutionBackupDirName!!
        )
        Assert.assertTrue(sourceExecutionBackupsDir.exists())

        val backedUpFile = File(sourceExecutionBackupsDir, fileName)
        assertExistsAndContains(backedUpFile, fileContents)
    }


    protected fun assertTargetTaskBackupsDirChildCount(count: Int) {
        Assert.assertEquals(count, taskBackupsDirInTarget.childrenCount)
    }

    protected fun assertTaskBackupsDirInSourceChildCount(count: Int) {
        Assert.assertEquals(count, taskBackupsDirInSource.childrenCount)
    }

    protected fun assertTargetExecutionBackupDirChildCount(count: Int) {
        Assert.assertEquals(count, executionBackupDirInTarget.childrenCount)
    }

    protected fun assertExecutionBackupDirInSourceChildCount(count: Int) {
        Assert.assertEquals(count, executionBackupDirInSource.childrenCount)
    }


    /**
     * Проверяет, что был забекаплен указанный файл,
     * и в каталогах бекапа нет лишнего.
     */
    protected fun assertOnlyFileWasBackupedInTarget(fileName: String, fileData: ByteArray) {
        assertTargetExecutionBackupDirExistsAndContains(fileName, fileData)
        assertTargetExecutionBackupDirChildCount(1)
        assertTargetTaskBackupsDirChildCount(1)
    }


    protected val taskBackupsDirInTarget: File
        get() = File(syncTask.targetTaskBackupsDirAbsolutePath!!)


    protected val taskBackupsDirInSource: File
        get() = File(syncTask.sourceTaskBackupsDirAbsolutePath!!)


    protected val executionBackupDirInTarget: File
        get() = File(taskBackupsDirInTarget, syncTask.targetExecutionBackupDirName!!)

    protected val executionBackupDirInSource: File
        get() = File(taskBackupsDirInSource, syncTask.sourceExecutionBackupDirName!!)


    protected fun createSourceFileAndSyncItWithTarget() {

        fileHelper.createFileInSource(sFileName, sFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(0)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    // TODO: тестировать эту сборную функцию
    protected fun assertOnlyDeepFileExistsAndContainsInSource(deepDirName: String, fileName: String, fileData: ByteArray) {
        assertDeepDirHasOnlyOneChildAtAllLevels(taskConfig.SOURCE_DIR, deepDirName)
        assertExistsAndContains(fileHelper.deepFileInSource(deepDirName, fileName), fileData)
    }

    // TODO: тестировать эту сборную функцию
    protected fun assertOnlyDeepFileExistsAndContainsInTarget(deepDirName: String, fileName: String, fileData: ByteArray) {
        assertDeepDirHasOnlyOneChildAtAllLevels(taskConfig.TARGET_DIR, deepDirName)
        assertExistsAndContains(fileHelper.deepFileInTarget(deepDirName, fileName), fileData)
    }


    // TODO: тестировать эту сборную функцию
    protected fun assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(
        deepDirName: String,
        fileName: String,
        fileData: ByteArray
    ) {
        val backupedDeepDirName = listOf<String>(
            syncTask.targetTaskBackupDirName!!,
            syncTask.targetExecutionBackupDirName!!,
            deepDirName
        ).joinToString(CloudWriter.DS)

        assertOnlyDeepFileExistsAndContainsInTarget(
            backupedDeepDirName,
            fileName,
            fileData
        )
    }
}