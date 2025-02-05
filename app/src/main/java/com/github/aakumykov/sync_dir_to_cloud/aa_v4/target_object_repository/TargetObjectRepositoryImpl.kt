package com.github.aakumykov.sync_dir_to_cloud.aa_v4.target_object_repository

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TargetObject
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TargetObjectDAO
import javax.inject.Inject

class TargetObjectRepositoryImpl @Inject constructor(
    private val targetObjectDAO: TargetObjectDAO
)
    : TargetObjectRepository
{
    override suspend fun add(targetObject: TargetObject) {
        targetObjectDAO.add(targetObject)
    }
}