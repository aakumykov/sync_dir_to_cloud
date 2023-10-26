package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.Embedded
import androidx.room.Relation

class SyncTaskWithCloudAuth(
    @Embedded
    val syncTask: SyncTask,

    @Relation(parentColumn = "cloud_auth_id", entityColumn = "id")
    val cloudAuth: CloudAuth
)