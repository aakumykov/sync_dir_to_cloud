package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName

abstract class NoBackupSyncTestBase : SyncTestBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()

    protected val sFileName = randomName
    protected val tFileName = randomName

    protected val sFile = fileHelper.fileInSource(sFileName)
    protected val tFile = fileHelper.fileInTarget(sFileName)

    protected val sFileInTarget = fileHelper.fileInTarget(sFileName)
    protected val tFileInSource = fileHelper.fileInSource(sFileName)

    protected val emptyData: ByteArray = byteArrayOf()
    protected val notEmptyData: ByteArray = randomBytes
}