package com.github.aakumykov.sync_dir_to_cloud.config

class BackupConfig {
    companion object {
        @Deprecated("Не нужно") const val BACKUP_DIR_BASE_PATH = "/"
        const val BACKUPS_TOP_DIR_PREFIX = "BACKUPS"
        @Deprecated("Переименовать в BACKUPS_DIR_PREFIX") const val BACKUP_DIR_PREFIX = "backup"
        const val BACKUP_DIR_DATE_TIME_FORMAT = "yyyy.MM.dd_HH-mm-ss"
        const val BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT = 5
    }
}
