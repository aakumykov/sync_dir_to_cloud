package com.github.aakumykov.sync_dir_to_cloud.app_settings

// FIXME: чтение и запись должны быть разделены
interface AppSettings {
    var restoreLostSourceAndTargetDirs: Boolean
    var fileTransferRetardationMs: Int
    var backupIsCriticalOperation: Boolean
    var dryRun: Boolean
}