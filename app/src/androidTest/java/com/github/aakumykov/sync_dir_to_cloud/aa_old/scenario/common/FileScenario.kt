package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.TestFilesManager
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class FileScenario() : Scenario() {
    protected val testFilesManager by lazy { TestFilesManager(LocalTaskConfig, LocalFileCofnig) }
}