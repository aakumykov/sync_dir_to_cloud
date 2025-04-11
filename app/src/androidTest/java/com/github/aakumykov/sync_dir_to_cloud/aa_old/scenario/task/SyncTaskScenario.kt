package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.task

import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.DaoSet
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class SyncTaskScenario(private val daoSet: DaoSet) : Scenario() {
    protected val syncTaskDAO
        get() = daoSet.syncTaskDAO

    protected val cloudAuthDAO
        get() = daoSet.cloudAuthDAO
}