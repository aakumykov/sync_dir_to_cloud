package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_80_comparison

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.intersectBy
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.extensions.subtractBy
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SourceWithTargetComparator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncObjectReader: SyncObjectReader,
    private val comparisonStateRepository: ComparisonStateRepository,
) {
    suspend fun compareSourceWithTarget() {

        val sourceObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.SOURCE, syncTask.id)
            .toMutableList()

        val targetObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.TARGET, syncTask.id)
            .toMutableList()

        val both = sourceObjectsList.intersectBy(targetObjectsList, ::areObjectsTheSame)
        val onlyInSource = sourceObjectsList.subtractBy(targetObjectsList, ::areObjectsTheSame)
        val onlyInTarget = targetObjectsList.subtractBy(sourceObjectsList, ::areObjectsTheSame)

        both.forEach { commonSyncObject ->
            val sourceObject = sourceObjectsList.first { commonSyncObject.isSameWith(it) }
            val targetObject = targetObjectsList.first { commonSyncObject.isSameWith(it) }
            comparisonStateRepository.add(ComparisonState(
                id = randomUUID,
                taskId = syncTask.id,
                executionId = executionId,
                isDir = commonSyncObject.isDir,
                sourceObjectId = sourceObject.id,
                targetObjectId = targetObject.id,
                sourceObjectState = sourceObject.stateInStorage,
                targetObjectState = targetObject.stateInStorage,
                relativePath = sourceObject.relativePath,
            ))
        }

        onlyInSource.forEach { sourceObject ->
            comparisonStateRepository.add(ComparisonState(
                id = randomUUID,
                taskId = syncTask.id,
                executionId = executionId,
                isDir = sourceObject.isDir,
                sourceObjectId = sourceObject.id,
                targetObjectId = null,
                sourceObjectState = sourceObject.stateInStorage,
                targetObjectState = null,
                relativePath = sourceObject.relativePath
            ))
        }

        onlyInTarget.forEach { targetObject ->
            comparisonStateRepository.add(ComparisonState(
                id = randomUUID,
                taskId = syncTask.id,
                executionId = executionId,
                isDir = targetObject.isDir,
                sourceObjectId = null,
                targetObjectId = targetObject.id,
                sourceObjectState = null,
                targetObjectState = targetObject.stateInStorage,
                relativePath = targetObject.relativePath
            ))
        }
    }

    private fun areObjectsTheSame(o1: SyncObject, o2: SyncObject): Boolean {
        return o1.isSameWith(o2)
    }
}


@AssistedFactory
interface SourceWithTargetComparatorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): SourceWithTargetComparator
}