package com.github.aakumykov.sync_dir_to_cloud.app_settings

interface AppSettingsReader {
    val isRestoreLostSourceAndTargetDirs: Boolean
}
