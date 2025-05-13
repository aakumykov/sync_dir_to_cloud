package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

val SyncTask.currentSourceBackupsDir: String?
    get() = sourcePath?.plus(
        sourceBackupDirName?.plus(
            sourceExecutionBackupDirName
        )
    )

val SyncTask.currentTargetBackupsDir: String?
    get() = targetPath?.plus(
        targetBackupDirName?.plus(
            targetExecutionBackupDirName
        )
    )