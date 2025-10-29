package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count.childrenCount
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceTaskBackupsDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetTaskBackupsDirPath
import org.junit.Assert
import java.io.File

abstract class BackupTestBase : SyncTestBase() {

    protected fun assertTargetExecutionBackupDirExistsAndContains(fileName: String, fileContents: ByteArray) {

        val targetExecutionBackupsDir = File(
            syncTask.targetTaskBackupsDirPath!!,
            syncTask.targetExecutionBackupDirName!!
        )
        Assert.assertTrue(targetExecutionBackupsDir.exists())

        val backedUpFile = File(targetExecutionBackupsDir, fileName)
        assertExistsAndContains(backedUpFile, fileContents)
    }

    protected fun assertExistsWithFilesCountInTarget(backupDir: File, count: Int) {
        Assert.assertTrue(backupDir.exists())
        Assert.assertEquals(count, backupDir.childrenCount)
    }


    protected fun assertTaskBackupsDirInTargetChildCount(count: Int) {
        Assert.assertEquals(count, taskBackupsDirInTarget.childrenCount)
    }

    protected fun assertTaskBackupsDirInSourceChildCount(count: Int) {
        Assert.assertEquals(count, taskBackupsDirInSource.childrenCount)
    }


    protected fun assertExecutionBackupDirInTargetChildCount(count: Int) {
        Assert.assertEquals(count, executionBackupDirInTarget.childrenCount)
    }

    protected fun assertExecutionBackupDirInSourceChildCount(count: Int) {
        Assert.assertEquals(count, executionBackupDirInSource.childrenCount)
    }


    protected val taskBackupsDirInTarget: File
        get() = File(syncTask.targetTaskBackupsDirPath!!)


    protected val taskBackupsDirInSource: File
        get() = File(syncTask.sourceTaskBackupsDirPath!!)


    protected val executionBackupDirInTarget: File
        get() = File(taskBackupsDirInTarget, syncTask.targetExecutionBackupDirName!!)

    protected val executionBackupDirInSource: File
        get() = File(taskBackupsDirInSource, syncTask.sourceExecutionBackupDirName!!)


    private val syncTask: SyncTask
        get() = TestComponentHolder.testSyncTaskDAO.get(taskConfig.TASK_ID)!!
}