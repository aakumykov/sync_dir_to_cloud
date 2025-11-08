package com.github.aakumykov.sync_dir_to_cloud.config

class BackupConfig {
    companion object {
        const val BACKUPS_TOP_DIR_PREFIX = "BACKUPS"
        const val BACKUPS_DIR_PREFIX = "backup"
        const val BACKUP_DIR_DATE_TIME_FORMAT = "yyyy.MM.dd_HH-mm-ss"
        // TODO: тестировать!
        const val BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT = 5
    }
}
