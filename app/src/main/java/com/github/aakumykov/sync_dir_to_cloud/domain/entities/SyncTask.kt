package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.Embedded
import androidx.room.Relation

class SyncTask (
    @Embedded
    val syncTaskBase: SyncTaskBase,

    @Relation(entity = CloudAuth::class, parentColumn = "auth_id", entityColumn = "id")
    val cloudAuth: CloudAuth
)