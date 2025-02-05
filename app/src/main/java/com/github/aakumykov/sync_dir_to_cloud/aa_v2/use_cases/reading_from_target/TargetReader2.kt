package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_target

import com.github.aakumykov.sync_dir_to_cloud.aa_v4.target_object_repository.TargetObjectRepository
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TargetObject
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class TargetReader2 @Inject constructor(
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val authReader: CloudAuthReader,
    private val targetObjectRepository: TargetObjectRepository,
) {
    // FIXME: избавиться от !!
    suspend fun readTarget(syncTask: SyncTask) {
        recursiveDirReaderFactory.create(
            storageType = syncTask.targetStorageType!!,
            authToken = authReader.getCloudAuth(syncTask.targetAuthId)!!.authToken
        )
            ?.listDirRecursively(syncTask.targetPath!!)
            ?.forEach { fileListItem ->
                targetObjectRepository.add(TargetObject.from(fileListItem))
            }
    }
}