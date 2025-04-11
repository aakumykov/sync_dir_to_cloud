package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common

import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.DaoSet
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class DbScenario(
    protected val daoSet: DaoSet
) : Scenario() {

    protected val syncTaskDAO get() = daoSet.syncTaskDAO
    protected val syncObjectDAO get() = daoSet.syncObjectDAO
    protected val syncObjectLogDao get() = daoSet.syncObjectLogDAO
    protected val cloudAuthDAO get() = daoSet.cloudAuthDAO
}