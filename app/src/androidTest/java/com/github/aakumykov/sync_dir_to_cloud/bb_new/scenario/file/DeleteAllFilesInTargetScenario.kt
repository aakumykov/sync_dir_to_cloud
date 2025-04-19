package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.DeleteAllFilesInDirScenario
import java.io.File

class DeleteAllFilesInTargetScenario : DeleteAllFilesInDirScenario() {
    override val dir: File
        get() = taskConfig.TARGET_DIR
}