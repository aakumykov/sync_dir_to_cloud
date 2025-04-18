package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common

import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.TestComponentHolder
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class DbScenario : Scenario() {
    protected val testSyncObjectDAO = TestComponentHolder.testSyncObjectDAO
}