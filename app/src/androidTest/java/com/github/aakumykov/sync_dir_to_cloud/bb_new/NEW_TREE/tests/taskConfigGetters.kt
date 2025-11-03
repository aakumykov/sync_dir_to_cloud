package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.UnreadableSourceLocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.ReadOnlyTargetLocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import java.io.File

val localToLocalNoBackupTaskConfig: TaskConfig
    get() = LocalToLocalSyncNoBackupTaskConfig()

val localToLocalWithBackupTaskConfig: TaskConfig
    get() = LocalToLocalSyncWithBackupTaskConfig(localToLocalNoBackupTaskConfig)

val unreadableSourceNoBackupTaskConfig: TaskConfig
    get() = UnreadableSourceLocalToLocalSyncNoBackupTaskConfig()

val readOnlyTargetNoBackupTaskConfig: TaskConfig
    get() = ReadOnlyTargetLocalToLocalSyncNoBackupTaskConfig(localToLocalNoBackupTaskConfig)

