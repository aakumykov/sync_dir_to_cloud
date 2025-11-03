package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path.FilePathSamples.ROOT_PATH
import java.io.File

class ReadOnlyTargetLocalToLocalSyncNoBackupTaskConfig(
    private val parentTaskConfig: TaskConfig
) : TaskConfig by parentTaskConfig {

    override val TARGET_DIR: File
        get() = File(ROOT_PATH)
}