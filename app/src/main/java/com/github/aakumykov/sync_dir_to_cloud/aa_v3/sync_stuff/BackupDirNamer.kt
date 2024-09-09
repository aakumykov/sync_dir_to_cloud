package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff

import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class BackupDirNamer @Inject constructor() {

    fun createBackupDirSpec(syncTask: SyncTask): BackupDirSpec {

        val dateSuffix = CurrentDateTime.format(syncTask.lastStart ?: Date().time)

        val backupDirName = "${BackupConfig.BACKUP_DIR_PREFIX}_${dateSuffix}"
        val backupParentDirPath = File(syncTask.targetPath!!, BackupConfig.BACKUPS_TOP_DIR_NAME).absolutePath

        return BackupDirSpec(
            backupDirName =  backupDirName,
            parentDirPath = backupParentDirPath,
        )
    }
}
