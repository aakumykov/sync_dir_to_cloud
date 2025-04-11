package com.github.aakumykov.sync_dir_to_cloud.scenario.aa_di_probe

import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import javax.inject.Inject

class QwertyScenario {

    @Inject
    lateinit var syncTaskDAO: SyncTaskDAO

    @Inject
    lateinit var syncObjectDAO: SyncObjectDAO

    @Inject
    lateinit var syncObjectLogDAO: SyncObjectLogDAO

    init {

    }
}