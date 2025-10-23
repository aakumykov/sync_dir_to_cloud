package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification.ModificationWithoutBackup.Companion.LOG_TAG
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Before
import java.io.File
import java.util.concurrent.TimeUnit

abstract class SyncTestBase : StorageAccessTestCase() {

    protected abstract val taskConfig: TaskConfig

    protected open val inDeviceChangesRelaxationTimeoutMs: Long = 1000

    protected val fileHelper get() = LocalFileHelper(taskConfig)


    //
    // Настройки файлов
    //

    protected val commonFileName = randomName
    protected val commonFileInSource = fileHelper.fileInSource(commonFileName)
    protected val commonFileInTarget = fileHelper.fileInTarget(commonFileName)

    protected val sFileName = randomName
    protected val tFileName = randomName

    protected val sFile = fileHelper.fileInSource(sFileName)
    protected val tFile = fileHelper.fileInTarget(tFileName)

    protected val commonFileData: ByteArray = freshNotEmptyData
    protected val sFileData: ByteArray = freshNotEmptyData
    protected val tFileData: ByteArray = freshNotEmptyData

    protected val newSourceFileData: ByteArray = freshNotEmptyData
    protected val newTargetFileData: ByteArray = freshNotEmptyData

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
    }


    protected fun doSync(delayAfterWork: Boolean = false) = run {
        println("$LOG_TAG: doSync()")

        scenario(RunSyncScenario(taskConfig.TASK_ID))

        if (delayAfterWork)
            TimeUnit.MILLISECONDS.sleep(inDeviceChangesRelaxationTimeoutMs)
    }

    protected fun assertExistsAndEmpty(fileOrDir: File) {
        Assert.assertTrue(fileOrDir.exists())
        Assert.assertTrue(fileOrDir.isEmpty)
    }

    protected fun assertExistsAndContains(file: File, contents: ByteArray) {
//        println("@@@@@@@@@@@@@@@@@@@ assertExistsAndContains() @@@@@@@@@@@@@@@@@@@@@@@@")
//        println("expected: ${contents.joinToString()}")
//        println("actual: ${fileHelper.getFileContents(file).joinToString()}")
//        println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
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
}