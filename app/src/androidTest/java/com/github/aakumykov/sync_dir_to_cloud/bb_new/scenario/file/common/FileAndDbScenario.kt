package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common

import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.TestComponentHolder

abstract class FileAndDbScenario : FileScenario() {
    protected val testSyncObjectDAO = TestComponentHolder.testSyncObjectDAO
}