package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalTestFilesConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper

abstract class LocalFileHelperTestBase : StorageAccessTestCase() {

    protected val fileConfig = LocalTestFilesConfig
    protected val taskConfig = LocalToLocalTaskConfig()
    protected val fileHelper = LocalFileHelper(taskConfig, fileConfig)
}