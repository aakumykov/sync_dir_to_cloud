package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

class SyncTask (

    @Embedded
    val task: SyncTaskBase,

    @Relation(entity = CloudAuth::class, parentColumn = "cloud_auth_id", entityColumn = "id")
    var cloudAuth: CloudAuth?
)
{
    @Ignore
    constructor() : this(SyncTaskBase(), null)

    companion object {
        val TAG: String = SyncTaskBase::class.java.simpleName
    }
}