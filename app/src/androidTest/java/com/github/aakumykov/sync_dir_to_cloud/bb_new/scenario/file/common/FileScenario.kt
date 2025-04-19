package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class FileScenario : Scenario() {
    val fileHelper = LocalFileHelper()
    val taskConfig = LocalTaskConfig()
    val fileConfig = LocalFileCofnig
}