package com.github.aakumykov.sync_dir_to_cloud.app_settings

interface AppSettings {
    var restoreLostSourceAndTargetDirs: Boolean
    var fileTransferRetardationMs: Int
    var backupIsCriticalOperation: Boolean
}