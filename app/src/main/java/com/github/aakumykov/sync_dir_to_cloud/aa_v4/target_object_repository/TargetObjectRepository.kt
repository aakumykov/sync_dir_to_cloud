package com.github.aakumykov.sync_dir_to_cloud.aa_v4.target_object_repository

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TargetObject

interface TargetObjectRepository {
    suspend fun add(targetObject: TargetObject)
}

