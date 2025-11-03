package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import java.io.File

class ReadOnlyTargetLocalToLocalSyncNoBackupTaskConfig() : LocalToLocalSyncNoBackupTaskConfig() {
    override val TARGET_DIR: File = systemRootDir
}