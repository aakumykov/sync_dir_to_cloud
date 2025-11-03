package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.ReadOnlyTargetLocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.UnreadableSourceLocalToLocalSyncNoBackupTaskConfig

val localToLocalNoBackupTaskConfig: LocalToLocalSyncNoBackupTaskConfig
    get() = LocalToLocalSyncNoBackupTaskConfig()


val localToLocalWithBackupTaskConfig: LocalToLocalSyncNoBackupTaskConfig
    get() = LocalToLocalSyncWithBackupTaskConfig()


val unreadableSourceNoBackupTaskConfig: LocalToLocalSyncNoBackupTaskConfig
    get() = UnreadableSourceLocalToLocalSyncNoBackupTaskConfig()


val readOnlyTargetNoBackupTaskConfig: LocalToLocalSyncNoBackupTaskConfig
    get() = ReadOnlyTargetLocalToLocalSyncNoBackupTaskConfig()

