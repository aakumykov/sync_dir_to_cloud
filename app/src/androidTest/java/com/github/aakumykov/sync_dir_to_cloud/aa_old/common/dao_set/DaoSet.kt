package com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set

import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO

interface DaoSet {
    val cloudAuthDAO: CloudAuthDAO
    val syncTaskDAO: SyncTaskDAO
    val syncObjectDAO: SyncObjectDAO
    val syncObjectLogDAO: SyncObjectLogDAO
}