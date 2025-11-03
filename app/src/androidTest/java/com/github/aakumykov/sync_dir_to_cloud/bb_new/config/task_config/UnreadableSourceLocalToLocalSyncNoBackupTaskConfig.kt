package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path.FilePathSamples.ROOT_PATH
import java.io.File

class UnreadableSourceLocalToLocalSyncNoBackupTaskConfig() :
    LocalToLocalSyncNoBackupTaskConfig(
        SOURCE_DIR = systemRootDir,
        SKIP_CREATING_SOURCE_DIR = true,
    )