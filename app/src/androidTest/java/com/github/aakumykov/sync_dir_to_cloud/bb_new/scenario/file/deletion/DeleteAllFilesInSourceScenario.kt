package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion

import java.io.File

class DeleteAllFilesInSourceScenario : DeleteAllFilesInDirScenario() {
    override val dir: File
        get() = taskConfig.SOURCE_DIR
}