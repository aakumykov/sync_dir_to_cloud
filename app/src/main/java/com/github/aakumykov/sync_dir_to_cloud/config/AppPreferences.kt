package com.github.aakumykov.sync_dir_to_cloud.config

object AppPreferences {
    const val BACKUP_USE_EXISTING_DIR = true // TODO: поменять на false
    const val BACKUP_PRESERVE_EXISTING_DIR_CONTENTS = false // TODO: поменять на true
    const val BACKUPS_TOP_DIR_PREFIX = BackupConfig.BACKUPS_TOP_DIR_PREFIX
    const val BACKUPS_DIR_PREFIX = BackupConfig.BACKUP_DIR_PREFIX
    const val BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT = BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
}