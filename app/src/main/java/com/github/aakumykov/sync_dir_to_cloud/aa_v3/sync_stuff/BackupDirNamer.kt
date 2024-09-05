package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff

import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class BackupDirNamer {

    fun createBackupDirSpec(syncTask: SyncTask): BackupDirSpec {

        val dateSuffix = SimpleDateFormat(BackupConfig.BACKUP_DIR_DATE_TIME_FORMAT, Locale.getDefault()).format(syncTask.lastStart)
        val backupDirName = "${BackupConfig.BACKUP_DIR_PREFIX}_${dateSuffix}"
        val backupParentDirPath = File(syncTask.targetPath!!, BackupConfig.BACKUPS_TOP_DIR_NAME).absolutePath

        return BackupDirSpec(
            backupDirName =  backupDirName,
            parentDirPath = backupParentDirPath,
        )
    }
}
