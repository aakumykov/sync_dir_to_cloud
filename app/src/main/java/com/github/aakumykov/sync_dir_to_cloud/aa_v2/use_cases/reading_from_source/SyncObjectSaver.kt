package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_source

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncObjectSaver {
    fun storeSyncObject(syncObject: SyncObject)
}
